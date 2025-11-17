// src/components/RegisterForm.tsx
import { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { Container, Card, Form, Button, Alert, Row, Col } from "react-bootstrap";
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

    const updateField = (field: keyof RegisterRequest, value: string) => {
        setForm((prev) => ({ ...prev, [field]: value }));
    };

    const onSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        setError(null);
        setLoading(true);

        try {
            await authService.register(form);
            navigate("/login");
        } catch (err: any) {
            setError(err.message || "Registration failed");
        } finally {
            setLoading(false);
        }
    };

    return (
        <Container
            className="d-flex justify-content-center align-items-center"
            style={{ minHeight: "100vh" }}
        >
            <Card style={{ width: "100%", maxWidth: 500 }}>
                <Card.Body>
                    <h2 className="text-center mb-4">Register</h2>

                    {error && <Alert variant="danger">{error}</Alert>}

                    <Form onSubmit={onSubmit}>
                        <Form.Group className="mb-3" controlId="username">
                            <Form.Label>Username</Form.Label>
                            <Form.Control
                                type="text"
                                value={form.username}
                                onChange={(e) =>
                                    updateField("username", e.target.value)
                                }
                                required
                            />
                        </Form.Group>

                        <Form.Group className="mb-3" controlId="email">
                            <Form.Label>Email</Form.Label>
                            <Form.Control
                                type="email"
                                value={form.email}
                                onChange={(e) =>
                                    updateField("email", e.target.value)
                                }
                                required
                            />
                        </Form.Group>

                        <Row className="mb-3">
                            <Col>
                                <Form.Group controlId="name">
                                    <Form.Label>Name</Form.Label>
                                    <Form.Control
                                        type="text"
                                        value={form.name}
                                        onChange={(e) =>
                                            updateField("name", e.target.value)
                                        }
                                        required
                                    />
                                </Form.Group>
                            </Col>
                            <Col>
                                <Form.Group controlId="surname">
                                    <Form.Label>Surname</Form.Label>
                                    <Form.Control
                                        type="text"
                                        value={form.surname}
                                        onChange={(e) =>
                                            updateField(
                                                "surname",
                                                e.target.value
                                            )
                                        }
                                        required
                                    />
                                </Form.Group>
                            </Col>
                        </Row>

                        <Form.Group className="mb-4" controlId="password">
                            <Form.Label>Password</Form.Label>
                            <Form.Control
                                type="password"
                                value={form.password}
                                onChange={(e) =>
                                    updateField(
                                        "password",
                                        e.target.value
                                    )
                                }
                                required
                            />
                        </Form.Group>

                        <Button
                            variant="primary"
                            type="submit"
                            className="w-100"
                            disabled={loading}
                        >
                            {loading ? "Creating..." : "Register"}
                        </Button>
                    </Form>

                    <div className="text-center mt-3">
                        Have an account?{" "}
                        <Link to="/login">
                            Log in
                        </Link>
                    </div>
                </Card.Body>
            </Card>
        </Container>
    );
}
