// src/pages/GenerateDataPage.tsx
import { useState } from "react";
import {
    Container,
    Card,
    Form,
    Button,
    Alert,
    Row,
    Col,
} from "react-bootstrap";

const API_BASE_URL =
    import.meta.env.VITE_API_BASE_URL ?? "http://localhost:8080";
const ADMIN_BASE = `${API_BASE_URL}/api/admin`;

const GenerateDataPage = () => {
    const [userCount, setUserCount] = useState(5);
    const [projectCount, setProjectCount] = useState(3);
    const [ticketsPerUser, setTicketsPerUser] = useState(10);
    const [loading, setLoading] = useState(false);
    const [message, setMessage] = useState<string | null>(null);
    const [error, setError] = useState<string | null>(null);

    const onSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        setLoading(true);
        setMessage(null);
        setError(null);

        try {
            const token = localStorage.getItem("token");
            if (!token) {
                throw new Error("Not authenticated");
            }

            const res = await fetch(`${ADMIN_BASE}/generate-data`, {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                    Authorization: `Bearer ${token}`,
                },
                body: JSON.stringify({
                    userCount,
                    projectCount,
                    ticketsPerUser,
                }),
            });

            if (!res.ok) {
                const text = await res.text();
                throw new Error(text || "Failed to generate data");
            }

            setMessage("Data successfully generated.");
        } catch (err: any) {
            setError(err.message || "Failed to generate data");
        } finally {
            setLoading(false);
        }
    };

    return (
        <Container className="mt-4">
            <Row className="justify-content-center">
                <Col md={6}>
                    <Card>
                        <Card.Body>
                            <Card.Title>Generate test data</Card.Title>

                            {message && <Alert variant="success">{message}</Alert>}
                            {error && <Alert variant="danger">{error}</Alert>}

                            <Form onSubmit={onSubmit}>
                                <Form.Group className="mt-2">
                                    <Form.Label>Number of users</Form.Label>
                                    <Form.Control
                                        type="number"
                                        min={1}
                                        value={userCount}
                                        onChange={(e) =>
                                            setUserCount(
                                                Number(e.target.value) || 0
                                            )
                                        }
                                    />
                                </Form.Group>

                                <Form.Group className="mt-2">
                                    <Form.Label>Number of projects</Form.Label>
                                    <Form.Control
                                        type="number"
                                        min={1}
                                        value={projectCount}
                                        onChange={(e) =>
                                            setProjectCount(
                                                Number(e.target.value) || 0
                                            )
                                        }
                                    />
                                </Form.Group>

                                <Form.Group className="mt-2">
                                    <Form.Label>
                                        Tickets per user per project
                                    </Form.Label>
                                    <Form.Control
                                        type="number"
                                        min={1}
                                        value={ticketsPerUser}
                                        onChange={(e) =>
                                            setTicketsPerUser(
                                                Number(e.target.value) || 0
                                            )
                                        }
                                    />
                                </Form.Group>

                                <Button
                                    className="mt-3"
                                    type="submit"
                                    variant="primary"
                                    disabled={loading}
                                >
                                    {loading ? "Generating..." : "Generate"}
                                </Button>
                            </Form>
                        </Card.Body>
                    </Card>
                </Col>
            </Row>
        </Container>
    );
};

export default GenerateDataPage;
