import type {
    LoginRequest,
    RegisterRequest,
    TokenResponse,
    RequestPasswordReset,
    ResetPassword,
    ChangePassword,
} from "../model/auth";
import type {ProjectResponse} from "./projectService.ts";

const API_BASE_URL =
    import.meta.env.VITE_API_BASE_URL ?? "http://localhost:8080";

const AUTH_BASE = `${API_BASE_URL}/api/auth`;

async function handleResponse(res: Response) {
    const contentType = res.headers.get("content-type") || "";
    const isJson = contentType.includes("application/json");

    // 204 NO_CONTENT
    if (res.status === 204) {
        if (!res.ok) {
            throw new Error("Request failed with 204 status");
        }
        return null;
    }

    const data = isJson ? await res.json() : await res.text();

    if (!res.ok) {
        const message =
            typeof data === "string" ? data : (data as any)?.message || "Request failed";
        throw new Error(message);
    }

    return data;
}

export const authService = {
    async login(payload: LoginRequest): Promise<TokenResponse> {
        const res = await fetch(`${AUTH_BASE}/login`, {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
            },
            body: JSON.stringify(payload),
        });

        return handleResponse(res) as Promise<TokenResponse>;
    },

    async register(payload: RegisterRequest): Promise<unknown> {
        const res = await fetch(`${AUTH_BASE}/register`, {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
            },
            body: JSON.stringify(payload),
        });

        return handleResponse(res);
    },

    async requestPasswordReset(payload: RequestPasswordReset): Promise<void> {
        const res = await fetch(`${AUTH_BASE}/request-password-reset`, {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
            },
            body: JSON.stringify(payload),
        });

        await handleResponse(res);
    },

    async resetPassword(payload: ResetPassword): Promise<void> {
        const res = await fetch(`${AUTH_BASE}/reset-password`, {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
            },
            body: JSON.stringify(payload),
        });

        await handleResponse(res);
    },

    async changePassword(payload: ChangePassword, token: string): Promise<void> {
        const res = await fetch(`${AUTH_BASE}/change-password`, {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
                Authorization: `Bearer ${token}`,
            },
            body: JSON.stringify(payload),
        });

        await handleResponse(res);
    },

    async getRole(token: string): Promise<string> {
        const res = await fetch(`${AUTH_BASE}/get-role`, {
            method: "GET",
            headers: {
                Authorization: `Bearer ${token}`,
            },
        });

        const data = await handleResponse(res);
        return data as string;
    },

};
