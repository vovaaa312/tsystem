import { useState } from "react";
import { authService } from "../services/authService.ts";

export default function ResetPassword() {
    const [code, setCode] = useState("");
    const [newPassword, setNewPassword] = useState("");
    const [message, setMessage] = useState("");

    const submit = async (e: any) => {
        e.preventDefault();
        try {
            await authService.resetPassword({ code, newPassword });
            setMessage("Password successfully reset.");
        } catch (err: any) {
            setMessage(err.message);
        }
    };

    return (
        <div className="container col-4 mt-5">
            <h3>Reset Password</h3>

            <form onSubmit={submit}>
                <input
                    className="form-control"
                    placeholder="Reset token"
                    value={code}
                    onChange={e => setCode(e.target.value)}
                />

                <input
                    type="password"
                    className="form-control mt-2"
                    placeholder="New password"
                    value={newPassword}
                    onChange={e => setNewPassword(e.target.value)}
                />

                <button className="btn btn-success mt-3" type="submit">
                    Reset Password
                </button>
            </form>

            {message && <div className="alert alert-info mt-3">{message}</div>}
        </div>
    );
}
