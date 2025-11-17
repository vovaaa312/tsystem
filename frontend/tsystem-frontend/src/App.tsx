// src/App.tsx
import "bootstrap/dist/css/bootstrap.min.css";
import {BrowserRouter, Routes, Route, Navigate} from "react-router-dom";
import {AuthProvider} from "./contexts/AuthContext";
import LoginForm from "./components/LoginForm";
import RegisterForm from "./components/RegisterForm";
import ProtectedRoute from "./components/ProtectedRoute";
import ProjectsPage from "./pages/ProjectsPage";
import TicketsPage from "./pages/TicketsPage";
import ResetRequest from "./pages/ResetRequest";
import ResetPassword from "./pages/ResetPassword";
import ChangePassword from "./pages/ChangePassword";
import Header from "./components/Header";
import GenerateDataPage from "./pages/GenerateDataPage";

function App() {
    return (
        <AuthProvider>
            <BrowserRouter>
                <Header/>
                <Routes>
                    <Route path="/login" element={<LoginForm/>}/>
                    <Route path="/register" element={<RegisterForm/>}/>
                    <Route
                        path="/reset-password-request"
                        element={<ResetRequest/>}
                    />
                    <Route
                        path="/reset-password"
                        element={<ResetPassword/>}
                    />

                    <Route element={<ProtectedRoute/>}>
                        <Route
                            path="/"
                            element={<Navigate to="/projects" replace/>}
                        />
                        <Route path="/projects" element={<ProjectsPage/>}/>
                        <Route
                            path="/projects/:projectId/tickets"
                            element={<TicketsPage />}
                        />
                        <Route path="/tickets" element={<TicketsPage/>}/>
                        <Route path="/admin/generate-data" element={<GenerateDataPage/>}/>
                        <Route path="/change-password" element={<ChangePassword/>}/>
                        <Route path="*" element={<Navigate to="/projects" replace/>}/>
                    </Route>
                </Routes>
            </BrowserRouter>
        </AuthProvider>
    );
}

export default App;
