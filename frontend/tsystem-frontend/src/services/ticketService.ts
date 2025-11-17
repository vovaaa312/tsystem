// src/services/ticketService.ts
const API_BASE_URL =
    import.meta.env.VITE_API_BASE_URL ?? "http://localhost:8080";

const PROJECTS_BASE = `${API_BASE_URL}/api/projects`;
const TICKETS_BASE = `${API_BASE_URL}/api/tickets`;

export interface TicketResponse {
    id: string;
    name: string;
    description?: string | null;
    type: string;
    priority: string;
    state: string;
    createdAt: string;
    projectId: string;
    userId: string;
    assignedUserId?: string | null;
}

export interface TicketRequestDto {
    name?: string;
    description?: string;
    type?: string;
    priority?: string;
    state?: string;
    assignedUserId?: string | null;
}

function getToken(): string {
    const token = localStorage.getItem("token");
    if (!token) {
        throw new Error("No authentication token found");
    }
    return token;
}

export function getCurrentUserId(): string {
    const token = getToken();
    const base64Url = token.split(".")[1];
    const base64 = base64Url.replace(/-/g, "+").replace(/_/g, "/");
    const decoded = JSON.parse(window.atob(base64));

    if (!decoded?.userId) {
        throw new Error("User ID not found in token");
    }
    return decoded.userId as string;
}

async function authFetch(url: string, options: RequestInit = {}) {
    const token = getToken();

    const headers: HeadersInit = {
        "Content-Type": "application/json",
        ...(options.headers || {}),
        Authorization: `Bearer ${token}`,
    };

    const res = await fetch(url, { ...options, headers });

    const contentType = res.headers.get("content-type") || "";
    const isJson = contentType.includes("application/json");
    const data = isJson ? await res.json() : await res.text();

    if (!res.ok) {
        const message =
            typeof data === "string"
                ? data
                : (data as any)?.message || "Request failed";
        throw new Error(message);
    }

    return data;
}

export const ticketService = {
    // GET /api/projects/{projectId}/tickets
    async findByProject(projectId: string): Promise<TicketResponse[]> {
        return authFetch(`${PROJECTS_BASE}/${projectId}/tickets`, {
            method: "GET",
        });
    },

    // GET /api/projects/{projectId}/tickets/{ticketId}
    async findOne(
        projectId: string,
        ticketId: string
    ): Promise<TicketResponse> {
        return authFetch(
            `${PROJECTS_BASE}/${projectId}/tickets/${ticketId}`,
            {method: "GET"}
        );
    },

    async findMyAll(): Promise<TicketResponse[]> {
        return authFetch(TICKETS_BASE, {method: "GET"});
    },


    async findMyAssigned(): Promise<TicketResponse[]> {
        return authFetch(TICKETS_BASE, { method: "GET" });
    },

    // POST /api/projects/{projectId}/tickets
    async create(
        projectId: string,
        payload: TicketRequestDto
    ): Promise<TicketResponse> {
        return authFetch(`${PROJECTS_BASE}/${projectId}/tickets`, {
            method: "POST",
            body: JSON.stringify(payload),
        });
    },

    // PUT /api/projects/{projectId}/tickets/{ticketId}
    async update(
        projectId: string,
        ticketId: string,
        payload: TicketRequestDto
    ): Promise<TicketResponse> {
        return authFetch(
            `${PROJECTS_BASE}/${projectId}/tickets/${ticketId}`,
            {
                method: "PUT",
                body: JSON.stringify(payload),
            }
        );
    },

    // PATCH /api/projects/{projectId}/tickets/{ticketId}
    async patch(
        projectId: string,
        ticketId: string,
        payload: TicketRequestDto
    ): Promise<TicketResponse> {
        return authFetch(
            `${PROJECTS_BASE}/${projectId}/tickets/${ticketId}`,
            {
                method: "PATCH",
                body: JSON.stringify(payload),
            }
        );
    },

    // DELETE /api/projects/{projectId}/tickets/{ticketId}
    async remove(projectId: string, ticketId: string): Promise<void> {
        await authFetch(
            `${PROJECTS_BASE}/${projectId}/tickets/${ticketId}`,
            {method: "DELETE"}
        );
    },
};
