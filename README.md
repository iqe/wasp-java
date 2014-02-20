# wasp-java

Java implementation of the wasp[^wasp] protocol.

## wasp data format

Wasp messages are delimited by `[` and ` ]` and contain a 16 bit checksum (CCITT). Escaping is done with `\`.

    [<message payload><16 bit CCITT>]

If `[`, `]` or `\` appear inside a message (payload or checksum), they are escaped by prefixing `\` and xor-ing the offending character with 0x20.

## predefined wasp messages

On top of the raw data format, wasp-java defines a set of messages tailored to its current usage of communicating with an Arduino:

- DigitalValueMessage to signal On/Off/Toggle
- AnalogValueMessage for 16 bit analog values
- HeartbeatMessage to build a heartbeat / timeout scheme
- PinConfigMessage to configure an Arduino

These messages are tightly coupled with a set of corresponding Arduino libraries that implement the other side of the wasp communication link.

[^wasp]: Why Another Serial Protocol?
