// src/components/Header.tsx
import { Navbar, Nav, Container, Button } from "react-bootstrap";
import { Link, useNavigate } from "react-router-dom";
import { useAuth } from "../contexts/AuthContext";

const Header = () => {
    const { token, isAuthenticated, logout } = useAuth();
    const navigate = useNavigate();

    const handleLogout = () => {
        logout();
        navigate("/login");
    };

    return (
        <Navbar bg="dark" variant="dark" expand="lg" className="mb-4">
            <Container>
                <Navbar.Brand as={Link} to="/projects">
                    TSystem
                </Navbar.Brand>

                <Navbar.Toggle aria-controls="main-navbar" />
                <Navbar.Collapse id="main-navbar">
                    {isAuthenticated && token && (
                        <>
                            <Nav className="me-auto">
                                <Nav.Link as={Link} to="/projects">
                                    Projects
                                </Nav.Link>
                                <Nav.Link as={Link} to="/tickets">
                                    My Tickets
                                </Nav.Link>
                                <Nav.Link as={Link} to="/change-password">
                                    Change Password
                                </Nav.Link>
                            </Nav>

                            <Nav>
                                <Button
                                    variant="outline-light"
                                    onClick={handleLogout}
                                >
                                    Logout
                                </Button>
                            </Nav>
                        </>
                    )}

                    {!isAuthenticated && (
                        <Nav className="ms-auto">
                            <Button
                                variant="outline-light"
                                className="me-2"
                                as={Link}
                                to="/login"
                            >
                                Login
                            </Button>
                            <Button
                                variant="outline-light"
                                as={Link}
                                to="/register"
                            >
                                Register
                            </Button>
                        </Nav>
                    )}
                </Navbar.Collapse>
            </Container>
        </Navbar>
    );
};

export default Header;
