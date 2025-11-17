import { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { Container, Card, Form, Button, Alert } from "react-bootstrap";
import type { LoginRequest, TokenResponse } from "../model/auth";
import { authService } from "../services/authService";
import { useAuth } from "../contexts/AuthContext";

export default function LoginForm() {
    const [form, setForm] = useState<LoginRequest>({ login: "", password: "" });
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState<string | null>(null);
    const { login: saveAuth } = useAuth();
    const navigate = useNavigate();

    const onSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        setLoading(true);
        setError(null);

        try {
            const tokenResponse: TokenResponse = await authService.login(form);
            const token = tokenResponse.token;


            // get role by token
            const role = await authService.getRole(token);

            // save token + role to context
            saveAuth(token, role);

            navigate("/projects");
        } catch (err: any) {
            setError(err.message || "Login failed");
        } finally {
            setLoading(false);
        }
    };

    return (
        <Container className="d-flex justify-content-center align-items-center mt-5">
            <Card style={{ width: "400px" }}>
                <Card.Body>
                    <Card.Title className="mb-4 text-center">Login</Card.Title>

                    {error && <Alert variant="danger">{error}</Alert>}

                    <Form onSubmit={onSubmit}>
                        <Form.Group controlId="login">
                            <Form.Label>Username or email</Form.Label>
                            <Form.Control
                                type="text"
                                value={form.login}
                                onChange={(e) =>
                                    setForm((f) => ({
                                        ...f,
                                        login: e.target.value,
                                    }))
                                }
                                required
                            />
                        </Form.Group>

                        <Form.Group controlId="password" className="mt-3">
                            <Form.Label>Password</Form.Label>
                            <Form.Control
                                type="password"
                                value={form.password}
                                onChange={(e) =>
                                    setForm((f) => ({
                                        ...f,
                                        password: e.target.value,
                                    }))
                                }
                                required
                            />
                        </Form.Group>

                        <Button
                            className="w-100 mt-4"
                            type="submit"
                            variant="primary"
                            disabled={loading}
                        >
                            {loading ? "Logging in..." : "Login"}
                        </Button>
                    </Form>

                    <div className="mt-3 text-center">
                        <span>Don't have an account? </span>
                        <Link to="/register">Register</Link>
                    </div>

                    <div className="mt-3 text-center">
                        <Link to="/reset-password-request">
                            Forgot password?
                        </Link>
                        <br />
                        <Link to="/reset-password">
                            Reset password (I have token)
                        </Link>
                    </div>
                </Card.Body>
            </Card>
        </Container>
    );
}
