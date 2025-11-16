// src/services/projectService.ts
const API_BASE_URL =
    import.meta.env.VITE_API_BASE_URL ?? "http://localhost:8080";

const PROJECT_BASE = `${API_BASE_URL}/api/projects`;

export interface ProjectResponse {
    id: string;
    name: string;
    description?: string | null;
    status: string;
    createdAt: string;
}

export interface ProjectCreateRequest {
    name: string;
    description?: string;
}

export interface ProjectUpdateRequest {
    name: string;
    description?: string;
    status: string;
}

export interface ProjectPatchRequest {
    name?: string;
    description?: string;
    status?: string;
}

async function authFetch(url: string, options: RequestInit = {}) {
    const token = localStorage.getItem("token");
    if (!token) {
        throw new Error("No authentication token found");
    }

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

export const projectService = {
    // GET /api/projects
    async findAll(): Promise<ProjectResponse[]> {
        return authFetch(PROJECT_BASE, { method: "GET" });
    },

    // GET /api/projects/{projectId}
    async findById(projectId: string): Promise<ProjectResponse> {
        return authFetch(`${PROJECT_BASE}/${projectId}`, { method: "GET" });
    },

    // POST /api/projects
    async create(payload: ProjectCreateRequest): Promise<ProjectResponse> {
        return authFetch(PROJECT_BASE, {
            method: "POST",
            body: JSON.stringify(payload),
        });
    },

    // PUT /api/projects/{projectId}
    async update(
        projectId: string,
        payload: ProjectUpdateRequest
    ): Promise<ProjectResponse> {
        return authFetch(`${PROJECT_BASE}/${projectId}`, {
            method: "PUT",
            body: JSON.stringify(payload),
        });
    },

    // PATCH /api/projects/{projectId}
    async patch(
        projectId: string,
        payload: ProjectPatchRequest
    ): Promise<ProjectResponse> {
        return authFetch(`${PROJECT_BASE}/${projectId}`, {
            method: "PATCH",
            body: JSON.stringify(payload),
        });
    },

    // DELETE /api/projects/{projectId}
    async remove(projectId: string): Promise<void> {
        await authFetch(`${PROJECT_BASE}/${projectId}`, { method: "DELETE" });
    },
};
