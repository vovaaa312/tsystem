// src/components/Header.tsx
import { Navbar, Nav, Container, Button } from "react-bootstrap";
import { Link, useNavigate } from "react-router-dom";
import { useAuth } from "../contexts/AuthContext";

const API_BASE_URL =
    import.meta.env.VITE_API_BASE_URL ?? "http://localhost:8080";
const ADMIN_BASE = `${API_BASE_URL}/api/admin`;

const Header = () => {
    const { isAuthenticated, role, logout } = useAuth();
    const navigate = useNavigate();

    const handleLogout = () => {
        logout();
        navigate("/login");
    };

    const handleExportJson = async () => {
        try {
            const token = localStorage.getItem("token");
            if (!token) {
                return;
            }

            const res = await fetch(`${ADMIN_BASE}/export-json`, {
                method: "GET",
                headers: {
                    Authorization: `Bearer ${token}`,
                },
            });

            if (!res.ok) {
                const text = await res.text();
                throw new Error(text || "Failed to export JSON");
            }

            const blob = await res.blob();
            const url = window.URL.createObjectURL(blob);
            const link = document.createElement("a");
            link.href = url;
            link.download = "export.json";
            document.body.appendChild(link);
            link.click();
            document.body.removeChild(link);
            window.URL.revokeObjectURL(url);
        } catch (e) {

            console.error(e);
        }
    };

    return (
        <Navbar bg="dark" variant="dark" expand="lg" className="mb-4">
            <Container>
                <Navbar.Brand as={Link} to="/projects">
                    Ticket System
                </Navbar.Brand>
                <Navbar.Toggle aria-controls="main-navbar" />
                <Navbar.Collapse id="main-navbar">
                    {isAuthenticated && (
                        <Nav className="me-auto">
                            <Nav.Link as={Link} to="/projects">
                                Projects
                            </Nav.Link>
                            <Nav.Link as={Link} to="/tickets">
                                Tickets
                            </Nav.Link>

                            {role === "SYSTEM_ADMIN" && (
                                <>
                                    <Nav.Link as={Link} to="/admin/generate-data">
                                        Generate data
                                    </Nav.Link>
                                    <Button
                                        variant="outline-light"
                                        size="sm"
                                        className="ms-2"
                                        onClick={handleExportJson}
                                    >
                                        Export JSON
                                    </Button>
                                </>
                            )}
                        </Nav>
                    )}

                    <Nav className="ms-auto">
                        {!isAuthenticated ? (
                            <>
                                <Nav.Link as={Link} to="/login">
                                    Login
                                </Nav.Link>
                                <Nav.Link as={Link} to="/register">
                                    Register
                                </Nav.Link>
                            </>
                        ) : (
                            <>
                                <Nav.Link as={Link} to="/change-password">
                                    Change password
                                </Nav.Link>
                                <Button
                                    variant="outline-light"
                                    size="sm"
                                    className="ms-2"
                                    onClick={handleLogout}
                                >
                                    Logout
                                </Button>
                            </>
                        )}
                    </Nav>
                </Navbar.Collapse>
            </Container>
        </Navbar>
    );
};

export default Header;
