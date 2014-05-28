# wasp-java

Java implementation of the wasp[^wasp] protocol.

## wasp data format

Wasp messages are delimited by `[` and ` ]` and contain a 16 bit checksum (CCITT). Escaping is done with `\`.

    [<message payload><16 bit CCITT>]

If `[`, `]` or `\` appear inside a message (payload or checksum), they are escaped by prefixing `\` and xor-ing the offending character with 0x20.

[^wasp]: Why Another Serial Protocol?
