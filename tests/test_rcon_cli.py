"""Regression test: rcon.py's __main__ block must run the CLI exactly once.

A previous edit appended a duplicate `if __name__ == '__main__'` block, which
made every command execute twice (the raw logs showed each command twice).
This test executes rcon.py as __main__ with a single command and mocks
socket.create_connection so no real RCON server is needed.
"""
import io
import runpy
import struct
import sys
import unittest
from unittest import mock


def _pkt(req_id, req_type, payload):
    body = struct.pack('<ii', req_id, req_type) + payload + b'\x00\x00'
    return struct.pack('<i', len(body)) + body


class RconCliTest(unittest.TestCase):
    def test_single_command_executes_once(self):
        # valid auth response (type 2), valid command response (type 0), then EOF
        auth = _pkt(1, 2, b'')
        resp = _pkt(1, 0, b'ok-payload')
        sock = mock.MagicMock()
        sock.recv.side_effect = [auth, resp, b'']
        with mock.patch('socket.create_connection', return_value=sock) as conn, \
             mock.patch('time.sleep'), \
             mock.patch.object(sys, 'argv', ['rcon.py', 'list']), \
             mock.patch('sys.stdout', new_callable=io.StringIO) as out:
            runpy.run_path('rcon.py', run_name='__main__')
        self.assertEqual(conn.call_count, 1, 'expected exactly one RCON connection')
        self.assertEqual(sock.send.call_count, 2, 'expected auth send + command send')
        self.assertEqual(sock.close.call_count, 1)
        stdout = out.getvalue()
        # one command echoed once, one response printed once
        self.assertEqual(stdout.count('> list'), 1)
        self.assertIn('ok-payload', stdout)


if __name__ == '__main__':
    unittest.main()
