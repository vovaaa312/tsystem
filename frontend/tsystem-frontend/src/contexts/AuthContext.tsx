import {
    createContext,
    useContext,
    useEffect,
    useState,
    ReactNode,
} from "react";

interface AuthContextType {
    token: string | null;
    isAuthenticated: boolean;
    login: (token: string) => void;
    logout: () => void;
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export const AuthProvider = ({ children }: { children: ReactNode }) => {
    const [token, setToken] = useState<string | null>(() =>
        typeof window !== "undefined" ? localStorage.getItem("token") : null
    );

    useEffect(() => {
        if (typeof window === "undefined") return;
        if (token) {
            localStorage.setItem("token", token);
        } else {
            localStorage.removeItem("token");
        }
    }, [token]);

    const value: AuthContextType = {
        token,
        isAuthenticated: !!token,
        login: (t: string) => setToken(t),
        logout: () => setToken(null),
    };

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
