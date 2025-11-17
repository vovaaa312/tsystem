// src/pages/TicketsPage.tsx
import { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import {
    Container,
    Table,
    Alert,
    Badge,
    Row,
    Col,
    Form,
    Button,
    Modal,
} from "react-bootstrap";
import {
    ticketService,
    type TicketResponse,
    getCurrentUserId,
    type TicketRequestDto,
} from "../services/ticketService";
import { useAuth } from "../contexts/AuthContext";

type TicketFormState = {
    id?: string;
    name: string;
    description: string;
    type: string;
    priority: string;
    state: string;
    assignedUserId: string;
};

const EMPTY_TICKET: TicketFormState = {
    name: "",
    description: "",
    type: "bug",
    priority: "med",
    state: "open",
    assignedUserId: "",
};

const TicketsPage = () => {
    const { projectId } = useParams<{ projectId: string }>();
    const [tickets, setTickets] = useState<TicketResponse[]>([]);
    const [error, setError] = useState<string | null>(null);
    const [loading, setLoading] = useState(true);
    const [onlyMineInProject, setOnlyMineInProject] = useState(true);
    const [currentUserId, setCurrentUserId] = useState<string | null>(null);

    const [showModal, setShowModal] = useState(false);
    const [form, setForm] = useState<TicketFormState>(EMPTY_TICKET);
    const [saving, setSaving] = useState(false);

    const navigate = useNavigate();
    const { logout } = useAuth();

    const handleAuthError = (e: any, fallback: string) => {
        if (
            e?.status === 401 ||
            e?.status === 403 ||
            e?.message === "Unauthorized" ||
            e?.message === "No authentication token found"
        ) {
            logout();
            navigate("/login");
            return;
        }
        setError(e?.message ?? fallback);
    };

    const loadTickets = async () => {
        try {
            setLoading(true);
            let data: TicketResponse[];

            if (projectId) {
                data = await ticketService.findByProject(projectId);
            } else {
                data = await ticketService.findMyAssigned();
            }

            setTickets(data);
            setError(null);
        } catch (e: any) {
            handleAuthError(e, "Failed to load tickets");
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        try {
            const id = getCurrentUserId();
            setCurrentUserId(id);
        } catch (e: any) {
            // если токен битый — выкидываем на логин
            handleAuthError(e, "Failed to read current user");
        }
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, []);

    useEffect(() => {
        loadTickets();
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, [projectId]);

    let visibleTickets = tickets;

    if (projectId && currentUserId && onlyMineInProject) {
        visibleTickets = tickets.filter(
            (t) =>
                t.userId === currentUserId ||
                t.assignedUserId === currentUserId
        );
    }

    const openCreateModal = () => {
        setForm(EMPTY_TICKET);
        setShowModal(true);
    };

    const openEditModal = (t: TicketResponse) => {
        setForm({
            id: t.id,
            name: t.name,
            description: t.description ?? "",
            type: t.type,
            priority: t.priority,
            state: t.state,
            assignedUserId: t.assignedUserId ?? "",
        });
        setShowModal(true);
    };

    const handleFormChange = (
        e: React.ChangeEvent<
            HTMLInputElement | HTMLSelectElement | HTMLTextAreaElement
        >
    ) => {
        const { name, value } = e.target;
        setForm((prev) => ({ ...prev, [name]: value }));
    };

    const handleSave = async (e: React.FormEvent) => {
        e.preventDefault();
        if (!projectId) return;

        try {
            setSaving(true);

            const payload: TicketRequestDto = {
                name: form.name,
                description: form.description,
                type: form.type,
                priority: form.priority,
                state: form.state,
                assignedUserId: form.assignedUserId || null,
            };

            if (form.id) {
                await ticketService.update(projectId, form.id, payload);
            } else {
                await ticketService.create(projectId, payload);
            }

            setShowModal(false);
            await loadTickets();
        } catch (e: any) {
            handleAuthError(e, "Failed to save ticket");
        } finally {
            setSaving(false);
        }
    };

    const handleDelete = async (t: TicketResponse) => {
        if (!projectId) return;
        if (!window.confirm(`Delete ticket "${t.name}"?`)) return;

        try {
            await ticketService.remove(projectId, t.id);
            await loadTickets();
        } catch (e: any) {
            handleAuthError(e, "Failed to delete ticket");
        }
    };

    return (
        <Container className="mt-4">
            <Row className="mb-3">
                <Col>
                    <h3>
                        {projectId
                            ? `Tickets for project ${projectId}`
                            : "My assigned tickets"}
                    </h3>
                </Col>
                <Col className="text-end">
                    {projectId && (
                        <Form.Check
                            type="switch"
                            id="only-mine-switch"
                            label="Only my tickets in this project"
                            checked={onlyMineInProject}
                            onChange={(e) =>
                                setOnlyMineInProject(e.currentTarget.checked)
                            }
                            className="d-inline-block me-3"
                        />
                    )}

                    {projectId && (
                        <Button variant="primary" onClick={openCreateModal}>
                            Add Ticket
                        </Button>
                    )}
                </Col>
            </Row>

            {error && <Alert variant="danger">{error}</Alert>}

            {loading ? (
                <p>Loading tickets...</p>
            ) : (
                <Table striped bordered hover>
                    <thead>
                    <tr>
                        <th>Name</th>
                        <th>Description</th>
                        <th>Type</th>
                        <th>Priority</th>
                        <th>State</th>
                        <th>Assigned user id</th>
                        <th>Created at</th>
                        {projectId && <th />}
                    </tr>
                    </thead>
                    <tbody>
                    {visibleTickets.map((t) => (
                        <tr key={t.id}>
                            <td>{t.name}</td>
                            <td>{t.description}</td>
                            <td>{t.type}</td>
                            <td>
                                <Badge bg="secondary">{t.priority}</Badge>
                            </td>
                            <td>
                                <Badge
                                    bg={
                                        t.state === "open"
                                            ? "success"
                                            : t.state === "in_progress"
                                                ? "warning"
                                                : "secondary"
                                    }
                                >
                                    {t.state}
                                </Badge>
                            </td>
                            <td>{t.assignedUserId ?? "—"}</td>
                            <td>
                                {t.createdAt
                                    ? new Date(
                                        t.createdAt
                                    ).toLocaleString()
                                    : "-"}
                            </td>
                            {projectId && (
                                <td className="text-nowrap">
                                    <Button
                                        size="sm"
                                        variant="outline-secondary"
                                        className="me-2"
                                        onClick={() => openEditModal(t)}
                                    >
                                        Edit
                                    </Button>
                                    <Button
                                        size="sm"
                                        variant="outline-danger"
                                        onClick={() => handleDelete(t)}
                                    >
                                        Delete
                                    </Button>
                                </td>
                            )}
                        </tr>
                    ))}
                    {visibleTickets.length === 0 && (
                        <tr>
                            <td
                                colSpan={projectId ? 8 : 7}
                                className="text-center"
                            >
                                No tickets to show.
                            </td>
                        </tr>
                    )}
                    </tbody>
                </Table>
            )}

            <Modal show={showModal} onHide={() => setShowModal(false)}>
                <Form onSubmit={handleSave}>
                    <Modal.Header closeButton>
                        <Modal.Title>
                            {form.id ? "Edit Ticket" : "Create Ticket"}
                        </Modal.Title>
                    </Modal.Header>
                    <Modal.Body>
                        <Form.Group className="mb-3">
                            <Form.Label>Name</Form.Label>
                            <Form.Control
                                name="name"
                                value={form.name}
                                onChange={handleFormChange}
                                required
                            />
                        </Form.Group>
                        <Form.Group className="mb-3">
                            <Form.Label>Description</Form.Label>
                            <Form.Control
                                as="textarea"
                                rows={3}
                                name="description"
                                value={form.description}
                                onChange={handleFormChange}
                            />
                        </Form.Group>
                        <Form.Group className="mb-3">
                            <Form.Label>Type</Form.Label>
                            <Form.Select
                                name="type"
                                value={form.type}
                                onChange={handleFormChange}
                            >
                                <option value="bug">bug</option>
                                <option value="task">task</option>
                                <option value="feature">feature</option>
                            </Form.Select>
                        </Form.Group>
                        <Form.Group className="mb-3">
                            <Form.Label>Priority</Form.Label>
                            <Form.Select
                                name="priority"
                                value={form.priority}
                                onChange={handleFormChange}
                            >
                                <option value="low">low</option>
                                <option value="med">med</option>
                                <option value="high">high</option>
                            </Form.Select>
                        </Form.Group>
                        <Form.Group className="mb-3">
                            <Form.Label>State</Form.Label>
                            <Form.Select
                                name="state"
                                value={form.state}
                                onChange={handleFormChange}
                            >
                                <option value="open">open</option>
                                <option value="in_progress">in_progress</option>
                                <option value="closed">closed</option>
                            </Form.Select>
                        </Form.Group>
                        <Form.Group className="mb-3">
                            <Form.Label>Assigned user id (UUID)</Form.Label>
                            <Form.Control
                                name="assignedUserId"
                                value={form.assignedUserId}
                                onChange={handleFormChange}
                                placeholder="Optional"
                            />
                        </Form.Group>
                    </Modal.Body>
                    <Modal.Footer>
                        <Button
                            variant="secondary"
                            onClick={() => setShowModal(false)}
                        >
                            Cancel
                        </Button>
                        <Button
                            variant="primary"
                            type="submit"
                            disabled={saving}
                        >
                            {saving ? "Saving..." : "Save"}
                        </Button>
                    </Modal.Footer>
                </Form>
            </Modal>
        </Container>
    );
};

export default TicketsPage;
