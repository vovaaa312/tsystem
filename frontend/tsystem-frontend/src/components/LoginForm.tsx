import { useState } from "react";
import { Link } from "react-router-dom";
import type { LoginRequest, TokenResponse } from "../model/auth";
import { authService } from "../services/authService";
import { useAuth } from "../contexts/AuthContext";

export default function LoginForm() {
    const [form, setForm] = useState<LoginRequest>({ login: "", password: "" });
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState<string | null>(null);
    const { login: saveToken } = useAuth();

    const onSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        setError(null);
        setLoading(true);

        try {
            const response: TokenResponse = await authService.login(form);

            alert("Login response:\n" + JSON.stringify(response, null, 2));

            if (response?.token) {
                saveToken(response.token);
            }
        } catch (err: any) {
            setError(err.message || "Login failed");
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="d-flex justify-content-center align-items-center min-vh-100 bg-light">
            <div className="card shadow-sm" style={{ minWidth: 360, maxWidth: 420, width: "100%" }}>
                <div className="card-body">
                    <h3 className="card-title mb-3 text-center">Login</h3>

                    {error && (
                        <div className="alert alert-danger py-2" role="alert">
                            {error}
                        </div>
                    )}

                    <form onSubmit={onSubmit}>
                        <div className="mb-3">
                            <label className="form-label">
                                Login (username or email)
                            </label>
                            <input
                                className="form-control"
                                value={form.login}
                                onChange={(e) =>
                                    setForm((prev) => ({ ...prev, login: e.target.value }))
                                }
                                required
                            />
                        </div>

                        <div className="mb-3">
                            <label className="form-label">Password</label>
                            <input
                                type="password"
                                className="form-control"
                                value={form.password}
                                onChange={(e) =>
                                    setForm((prev) => ({ ...prev, password: e.target.value }))
                                }
                                required
                            />
                        </div>

                        <button
                            type="submit"
                            className="btn btn-primary w-100"
                            disabled={loading}
                        >
                            {loading ? "Logging in..." : "Login"}
                        </button>
                    </form>

                    <p className="mt-3 text-center small mb-0">
                        No account?{" "}
                        <Link to="/register" className="link-primary">
                            Register
                        </Link>
                    </p>
                </div>
            </div>
        </div>
    );
}
