import socket, struct, sys, time

def rcon(cmd):
    s = socket.create_connection(('127.0.0.1', 25575), timeout=10)
    def pkt(t, p):
        b = struct.pack('<ii', 1, t) + p + b'\x00\x00'
        return struct.pack('<i', len(b)) + b
    s.send(pkt(3, b'test'))
    time.sleep(0.15)
    s.recv(8192)
    s.send(pkt(2, cmd.encode()))
    time.sleep(0.4)
    out = b''
    s.settimeout(1.5)
    try:
        while True:
            d = s.recv(65536)
            if not d:
                break
            out += d
    except socket.timeout:
        pass
    s.close()
    return out[12:-2].decode('utf-8', 'replace') if len(out) > 12 else repr(out)

if __name__ == '__main__':
    for cmd in sys.argv[1:]:
        print('> ' + cmd)
        print(rcon(cmd))
        print()
