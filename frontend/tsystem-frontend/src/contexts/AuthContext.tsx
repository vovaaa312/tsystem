// src/contexts/AuthContext.tsx
import {
    createContext,
    useContext,
    useState,
    ReactNode,
    useMemo,
} from "react";

interface AuthContextType {
    token: string | null;
    role: string | null;
    isAuthenticated: boolean;
    login: (token: string, role: string) => void;
    logout: () => void;
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export const AuthProvider = ({ children }: { children: ReactNode }) => {
    const [token, setToken] = useState<string | null>(
        () => localStorage.getItem("token")
    );
    const [role, setRole] = useState<string | null>(
        () => localStorage.getItem("role")
    );

    const isAuthenticated = !!token;

    const login = (newToken: string, newRole: string) => {
        setToken(newToken);
        setRole(newRole);
        localStorage.setItem("token", newToken);
        localStorage.setItem("role", newRole);
    };

    const logout = () => {
        setToken(null);
        setRole(null);
        localStorage.removeItem("token");
        localStorage.removeItem("role");
    };

    const value = useMemo(
        () => ({
            token,
            role,
            isAuthenticated,
            login,
            logout,
        }),
        [token, role, isAuthenticated]
    );

    return (
        <AuthContext.Provider value={value}>{children}</AuthContext.Provider>
    );
};

export const useAuth = () => {
    const ctx = useContext(AuthContext);
    if (!ctx) {
        throw new Error("useAuth must be used inside <AuthProvider>");
    }
    return ctx;
};
