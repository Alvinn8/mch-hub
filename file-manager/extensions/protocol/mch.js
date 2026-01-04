const DEFAULT_MCH_HUB_API_BASE = "http://backend:8080/api";

// The file-manager backend runs inside Docker. When developing with the mch-hub backend
// on your host, set MCH_HUB_API_BASE to e.g. "http://host.docker.internal:8080/api".
const MCH_HUB_API_BASE = (typeof process !== "undefined" && process.env && process.env.MCH_HUB_API_BASE)
    ? process.env.MCH_HUB_API_BASE
    : DEFAULT_MCH_HUB_API_BASE;

const URL_BASE = `${MCH_HUB_API_BASE.replace(/\/$/, "")}/repos/`;

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
