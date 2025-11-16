// src/pages/ProjectsPage.tsx
import {useEffect, useState} from "react";
import {useNavigate} from "react-router-dom";
import {
    Container,
    Table,
    Button,
    Row,
    Col,
    Form,
    Alert,
    Modal,
} from "react-bootstrap";
import {
    projectService,
    type ProjectResponse,
} from "../services/projectService";

type ProjectFormState = {
    id?: string;
    name: string;
    description: string;
    status: string;
};

const EMPTY_FORM: ProjectFormState = {
    name: "",
    description: "",
    status: "open",
};

const ProjectsPage = () => {
    const [projects, setProjects] = useState<ProjectResponse[]>([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState<string | null>(null);
    const [search, setSearch] = useState("");
    const [statusFilter, setStatusFilter] = useState<string>("all");

    const [showModal, setShowModal] = useState(false);
    const [form, setForm] = useState<ProjectFormState>(EMPTY_FORM);
    const [saving, setSaving] = useState(false);

    const navigate = useNavigate();

    const loadProjects = async () => {
        try {
            setLoading(true);
            const data = await projectService.findAll();
            setProjects(data);
            setError(null);
        } catch (e: any) {
            setError(e.message ?? "Failed to load projects");
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        loadProjects();
    }, []);

    const filtered = projects.filter((p) => {
        const matchesText =
            p.name.toLowerCase().includes(search.toLowerCase()) ||
            (p.description ?? "")
                .toLowerCase()
                .includes(search.toLowerCase());
        const matchesStatus =
            statusFilter === "all" || p.status === statusFilter;
        return matchesText && matchesStatus;
    });

    const openCreateModal = () => {
        setForm(EMPTY_FORM);
        setShowModal(true);
    };

    const openEditModal = (project: ProjectResponse) => {
        setForm({
            id: project.id,
            name: project.name,
            description: project.description ?? "",
            status: project.status,
        });
        setShowModal(true);
    };

    const handleFormChange = (
        e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement | HTMLTextAreaElement>
    ) => {
        const {name, value} = e.target;
        setForm((prev) => ({...prev, [name]: value}));
    };

    const handleSave = async (e: React.FormEvent) => {
        e.preventDefault();
        try {
            setSaving(true);
            if (form.id) {
                await projectService.update(form.id, {
                    name: form.name,
                    description: form.description,
                    status: form.status,
                });
            } else {
                await projectService.create({
                    name: form.name,
                    description: form.description,
                });
            }
            setShowModal(false);
            await loadProjects();
        } catch (e: any) {
            setError(e.message ?? "Failed to save project");
        } finally {
            setSaving(false);
        }
    };

    const handleDelete = async (project: ProjectResponse) => {
        if (!window.confirm(`Delete project "${project.name}"?`)) return;
        try {
            await projectService.remove(project.id);
            await loadProjects();
        } catch (e: any) {
            setError(e.message ?? "Failed to delete project");
        }
    };

    return (
        <Container className="mt-4">
            <Row className="mb-3">
                <Col>
                    <h2>My Projects</h2>
                </Col>
                <Col className="text-end">
                    <Button variant="primary" onClick={openCreateModal}>
                        Add Project
                    </Button>
                </Col>
            </Row>

            <Row className="mb-3">
                <Col md={6}>
                    <Form.Control
                        placeholder="Search by name or description"
                        value={search}
                        onChange={(e) => setSearch(e.target.value)}
                    />
                </Col>
                <Col md={3}>
                    <Form.Select
                        value={statusFilter}
                        onChange={(e) => setStatusFilter(e.target.value)}
                    >
                        <option value="all">All statuses</option>
                        <option value="open">Open</option>
                        <option value="in_progress">In progress</option>
                        <option value="closed">Closed</option>
                    </Form.Select>
                </Col>
            </Row>

            {error && <Alert variant="danger">{error}</Alert>}

            {loading ? (
                <p>Loading projects...</p>
            ) : (
                <Table striped bordered hover>
                    <thead>
                    <tr>
                        <th>Name</th>
                        <th>Description</th>
                        <th>Status</th>
                        <th>Created at</th>
                        <th/>
                    </tr>
                    </thead>
                    <tbody>
                    {filtered.map((p) => (
                        <tr key={p.id}>
                            <td>
                                <Button
                                    variant="link"
                                    className="p-0 align-baseline"
                                    onClick={() =>
                                        navigate(`/projects/${p.id}/tickets`)
                                    }
                                >
                                    {p.name}
                                </Button>
                            </td>
                            <td>{p.description}</td>
                            <td>{p.status}</td>
                            <td>
                                {p.createdAt
                                    ? new Date(p.createdAt).toLocaleString()
                                    : "-"}
                            </td>
                            <td className="text-nowrap">
                                <Button
                                    size="sm"
                                    variant="outline-secondary"
                                    className="me-2"
                                    onClick={() => openEditModal(p)}
                                >
                                    Edit
                                </Button>
                                <Button
                                    size="sm"
                                    variant="outline-danger"
                                    onClick={() => handleDelete(p)}
                                >
                                    Delete
                                </Button>
                            </td>
                        </tr>
                    ))}
                    {filtered.length === 0 && (
                        <tr>
                            <td colSpan={5} className="text-center">
                                No projects found.
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
                            {form.id ? "Edit Project" : "Create Project"}
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
                        {form.id && (
                            <Form.Group className="mb-3">
                                <Form.Label>Status</Form.Label>
                                <Form.Select
                                    name="status"
                                    value={form.status}
                                    onChange={handleFormChange}
                                >
                                    <option value="open">Open</option>
                                    <option value="in_progress">
                                        In progress
                                    </option>
                                    <option value="closed">Closed</option>
                                </Form.Select>
                            </Form.Group>
                        )}
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

export default ProjectsPage;
