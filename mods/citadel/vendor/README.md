# citadel-video-deps.jar — provenance

Private runtime dependency for `com.github.alexthe666.citadel.client.video.Video`
(Citadel's public video API, restored verbatim from upstream).

## Contents

- `org/jcodec/**` — JCodec 0.2.5 class tree, unmodified
- `net/sourceforge/jaad/**` — JAADec AAC decoder class tree, unmodified
- `fabric.mod.json` — added here (id `citadel_video_deps`) so Fabric Loader
  loads the jar from `META-INF/jars/` inside citadel-2.7.1.jar.

## Source

Extracted from the official released Citadel jar:

- Path: `C:/Users/nonza/Downloads/citadel-2.7.0-1.21.1.jar`
- SHA-256: `a80af1dbc6d00ea908366bcc175951a11fbf83a12aeadd36b5035a772ff7c3a6`
- The donor jar ships both trees pre-shaded; no license files for jcodec or
  JAADec are present inside the donor jar.
- Upstream build.gradle references `org.jcodec:jcodec:0.2.5` and
  `com.github.AlexModGuy:JAADec:master-SNAPSHOT`; the cached jars were not
  available offline, so the shaded classes were extracted byte-for-byte.

## Licensing

LicenseRef-Upstream-Citadel-Video-Dependencies — no license text was found
inside the donor jar for these trees; no redistribution permission is claimed.
This jar is for private use only.
