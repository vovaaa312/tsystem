// src/pages/ChangePassword.tsx
import { useState } from "react";
import { Container, Card, Form, Button, Alert } from "react-bootstrap";
import { authService } from "../services/authService";
import {useAuth} from "../contexts/AuthContext.tsx";
import {useNavigate} from "react-router-dom";

const ChangePassword = () => {
    const [oldPassword, setOldPassword] = useState("");
    const [newPassword, setNewPassword] = useState("");
    const [confirmNewPassword, setConfirmNewPassword] = useState("");
    const [error, setError] = useState<string | null>(null);
    const [success, setSuccess] = useState<string | null>(null);
    const [loading, setLoading] = useState(false);
    const { token, isAuthenticated, logout } = useAuth();
    const navigate = useNavigate();

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        setError(null);
        setSuccess(null);

        if (newPassword !== confirmNewPassword) {
            setError("New passwords do not match");
            return;
        }

        const token = localStorage.getItem("token");
        if (!token) {
            setError("You are not authenticated");
            return;
        }

        try {
            setLoading(true);
            await authService.changePassword(
                { oldPassword, newPassword } as any,
                token
            );
            setSuccess("Password changed successfully");

            setOldPassword("");
            setNewPassword("");
            setConfirmNewPassword("");
            handleLogout()
        } catch (err) {
            setError(
                err instanceof Error ? err.message : "Failed to change password"
            );
        } finally {
            setLoading(false);
        }
    };

    const handleLogout = () => {
        logout();
        navigate("/login");
    };

    return (
        <Container className="mt-4" style={{ maxWidth: "500px" }}>
            <Card>
                <Card.Body>
                    <Card.Title>Change Password</Card.Title>

                    {error && <Alert variant="danger">{error}</Alert>}
                    {success && <Alert variant="success">{success}</Alert>}

                    <Form onSubmit={handleSubmit}>
                        <Form.Group className="mb-3" controlId="oldPassword">
                            <Form.Label>Current password</Form.Label>
                            <Form.Control
                                type="password"
                                value={oldPassword}
                                onChange={(e) =>
                                    setOldPassword(e.target.value)
                                }
                                required
                            />
                        </Form.Group>

                        <Form.Group className="mb-3" controlId="newPassword">
                            <Form.Label>New password</Form.Label>
                            <Form.Control
                                type="password"
                                value={newPassword}
                                onChange={(e) =>
                                    setNewPassword(e.target.value)
                                }
                                required
                            />
                        </Form.Group>

                        <Form.Group
                            className="mb-3"
                            controlId="confirmNewPassword"
                        >
                            <Form.Label>Confirm new password</Form.Label>
                            <Form.Control
                                type="password"
                                value={confirmNewPassword}
                                onChange={(e) =>
                                    setConfirmNewPassword(e.target.value)
                                }
                                required
                            />
                        </Form.Group>

                        <Button
                            variant="primary"
                            type="submit"
                            disabled={loading}
                        >
                            {loading ? "Changing..." : "Change password"}
                        </Button>
                    </Form>
                </Card.Body>
            </Card>
        </Container>
    );
};

export default ChangePassword;
