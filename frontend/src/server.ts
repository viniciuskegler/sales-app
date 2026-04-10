import { APP_BASE_HREF } from "@angular/common";
import { CommonEngine, isMainModule } from "@angular/ssr/node";
import express from "express";
import { createProxyMiddleware } from "http-proxy-middleware";
import { dirname, join, resolve } from "node:path";
import { fileURLToPath } from "node:url";
import bootstrap from "./main.server";

const serverDistFolder = dirname(fileURLToPath(import.meta.url));
const browserDistFolder = resolve(serverDistFolder, "../browser");
const indexHtml = join(serverDistFolder, "index.server.html");

const backendUrl = process.env["BACKEND_URL"] || "http://localhost:8080";

const app = express();
const commonEngine = new CommonEngine();

const backendProxy = createProxyMiddleware({
    target: backendUrl,
    changeOrigin: true,
});

/**
 * Proxy /api/** and /ws to the backend service.
 * Must be registered before the static files handler.
 */
app.use("/api", backendProxy);
app.use("/ws", backendProxy);

/**
 * Serve static files from /browser
 */
app.get(
    "**",
    express.static(browserDistFolder, {
        maxAge: "1y",
        index: "index.html",
    }),
);

/**
 * Handle all other requests by rendering the Angular application.
 */
app.get("**", (req, res, next) => {
    const { protocol, originalUrl, baseUrl, headers } = req;

    commonEngine
        .render({
            bootstrap,
            documentFilePath: indexHtml,
            url: `${protocol}://${headers.host}${originalUrl}`,
            publicPath: browserDistFolder,
            providers: [{ provide: APP_BASE_HREF, useValue: baseUrl }],
        })
        .then((html) => res.send(html))
        .catch((err) => next(err));
});

/**
 * Start the server if this module is the main entry point.
 * The server listens on the port defined by the `PORT` environment variable, or defaults to 4000.
 */
if (isMainModule(import.meta.url)) {
    const port = process.env["PORT"] || 4000;
    const server = app.listen(port, () => {
        console.log(
            `Node Express server listening on http://localhost:${port}`,
        );
    });

    // Proxy WebSocket upgrade requests (/ws) to the backend
    server.on("upgrade", (req, socket, head) => {
        if (req.url?.startsWith("/ws")) {
            // @ts-expect-error upgrade exists at runtime
            backendProxy.upgrade(req, socket, head);
        }
    });
}

export default app;
