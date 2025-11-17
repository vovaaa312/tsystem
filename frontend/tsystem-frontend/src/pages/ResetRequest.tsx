import { useState } from "react";
import { authService } from "../services/authService.ts";

export default function ResetRequest() {
    const [email, setEmail] = useState("");
    const [message, setMessage] = useState("");

    const submit = async (e: any) => {
        e.preventDefault();
        try {
            await authService.requestPasswordReset({ email });
            setMessage("Reset token was generated. Check server logs.");

        } catch (err: any) {
            setMessage(err.message);
        }
    };

    return (
        <div className="container col-4 mt-5">
            <h3>Password Reset Request</h3>

            <form onSubmit={submit}>
                <input
                    className="form-control"
                    placeholder="Your email"
                    value={email}
                    onChange={e => setEmail(e.target.value)}
                />

                <button className="btn btn-primary mt-3" type="submit">
                    Request Reset
                </button>
            </form>

            {message && <div className="alert alert-info mt-3">{message}</div>}
        </div>
    );
}
