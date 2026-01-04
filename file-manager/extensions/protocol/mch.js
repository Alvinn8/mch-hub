const URL_BASE = "http://backend:8080/api/repos/";

function ensure200(response) {
    if (!response.ok) {
        throw new Error(`HTTP ${response.status}`);
    }
    return response;
}

export default function setup({ handler, Packets, ServerPackets, FileType }) {
    handler(Packets.Connect, async (packet, data, connection) => {
        connection.client = {
            url: `${URL_BASE}${data.repository}/files/`,
            commit: data.commit,
        };

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
        const commit = encodeURIComponent(connection.client.commit);
        const path = encodeURIComponent(data.path);
        return await fetch(connection.client.url + "list?commit=" + commit + "&path=" + path)
            .then(ensure200)
            .then(res => res.json())
            .then(res => ({ files: res }));
    });
}
