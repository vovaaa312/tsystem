// src/pages/ResetPassword.tsx
import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { authService } from "../services/authService.ts";
import { useAuth } from "../contexts/AuthContext";

export default function ResetPassword() {
    const [code, setCode] = useState("");
    const [newPassword, setNewPassword] = useState("");
    const [message, setMessage] = useState("");
    const [error, setError] = useState<string | null>(null);

    const navigate = useNavigate();
    const { logout } = useAuth();

    const submit = async (e: React.FormEvent) => {
        e.preventDefault();
        setError(null);
        setMessage("");

        try {
            await authService.resetPassword({ code, newPassword });
            // после сброса старый токен больше невалиден
            logout();
            setMessage("Password successfully reset. Please log in again.");
            navigate("/login");
        } catch (err: any) {
            setError(err.message || "Failed to reset password");
        }
    };

    return (
        <div className="container col-4 mt-5">
            <h3>Reset Password</h3>

            {error && <div className="alert alert-danger mt-3">{error}</div>}
            {message && (
                <div className="alert alert-info mt-3">{message}</div>
            )}

            <form onSubmit={submit}>
                <input
                    className="form-control"
                    placeholder="Reset token"
                    value={code}
                    onChange={(e) => setCode(e.target.value)}
                    required
                />

                <input
                    type="password"
                    className="form-control mt-2"
                    placeholder="New password"
                    value={newPassword}
                    onChange={(e) => setNewPassword(e.target.value)}
                    required
                />

                <button className="btn btn-success mt-3" type="submit">
                    Reset Password
                </button>
            </form>
        </div>
    );
}
