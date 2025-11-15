import { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import type { RegisterRequest } from "../model/auth";
import { authService } from "../services/authService";

export default function RegisterForm() {
    const [form, setForm] = useState<RegisterRequest>({
        username: "",
        email: "",
        name: "",
        surname: "",
        password: "",
    });

    const [loading, setLoading] = useState(false);
    const [error, setError] = useState<string | null>(null);
    const navigate = useNavigate();

    const onSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        setError(null);
        setLoading(true);

        try {
            const response = await authService.register(form);

            alert("Register response:\n" + JSON.stringify(response, null, 2));

            navigate("/login");
        } catch (err: any) {
            setError(err.message || "Registration failed");
        } finally {
            setLoading(false);
        }
    };

    const updateField = (field: keyof RegisterRequest, value: string) => {
        setForm((prev) => ({ ...prev, [field]: value }));
    };

    return (
        <div style={{ maxWidth: 500, margin: "40px auto" }}>
            <h2>Register</h2>

            {error && (
                <div style={{ color: "red", marginBottom: 8 }}>
                    {error}
                </div>
            )}

            <form onSubmit={onSubmit}>
                <div style={{ marginBottom: 8 }}>
                    <label style={{ display: "block", marginBottom: 4 }}>Username</label>
                    <input
                        style={{ width: "100%", padding: 4 }}
                        value={form.username}
                        onChange={(e) => updateField("username", e.target.value)}
                        required
                    />
                </div>

                <div style={{ marginBottom: 8 }}>
                    <label style={{ display: "block", marginBottom: 4 }}>Email</label>
                    <input
                        type="email"
                        style={{ width: "100%", padding: 4 }}
                        value={form.email}
                        onChange={(e) => updateField("email", e.target.value)}
                        required
                    />
                </div>

                <div style={{ display: "flex", gap: 8, marginBottom: 8 }}>
                    <div style={{ flex: 1 }}>
                        <label style={{ display: "block", marginBottom: 4 }}>Name</label>
                        <input
                            style={{ width: "100%", padding: 4 }}
                            value={form.name}
                            onChange={(e) => updateField("name", e.target.value)}
                            required
                        />
                    </div>

                    <div style={{ flex: 1 }}>
                        <label style={{ display: "block", marginBottom: 4 }}>Surname</label>
                        <input
                            style={{ width: "100%", padding: 4 }}
                            value={form.surname}
                            onChange={(e) => updateField("surname", e.target.value)}
                            required
                        />
                    </div>
                </div>

                <div style={{ marginBottom: 8 }}>
                    <label style={{ display: "block", marginBottom: 4 }}>Password</label>
                    <input
                        type="password"
                        style={{ width: "100%", padding: 4 }}
                        value={form.password}
                        onChange={(e) => updateField("password", e.target.value)}
                        required
                    />
                </div>

                <button type="submit" disabled={loading}>
                    {loading ? "Creating..." : "Register"}
                </button>
            </form>

            <p style={{ marginTop: 8 }}>
                Have an account?{" "}
                <Link to="/login">
                    Log in
                </Link>
            </p>
        </div>
    );
}
