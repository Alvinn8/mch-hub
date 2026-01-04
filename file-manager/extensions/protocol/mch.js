export default function setup({ handler, Packets, ServerPackets, FileType }) {
    handler(Packets.Connect, async (packet, data, connection) => {
        connection.client = data;

        return {
            readOnly: true,
        };
    });

    handler(Packets.Ping, async (packet, data, connection) => {
        return {
            isConnected: true,
        };
    });

    handler(ServerPackets.IsConnected, (packet, data, connection) => {
        // This handler is not allowed to be async.
        return {
            isConnected: true,
        };
    });

    handler(Packets.List, async (packet, data, connection) => {
        return {
            files: [
                {
                    name: "example.txt",
                    size: 1234,
                    type: FileType.File,
                    rawModifiedAt: "today",
                },
                {
                    name: "documents",
                    size: 0,
                    type: FileType.Directory,
                    rawModifiedAt: "yesterday",
                },
            ],
        };
    });
}
