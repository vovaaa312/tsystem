// src/model/auth.ts

export interface RegisterRequest {
    username: string;
    email: string;
    name: string;
    surname: string;
    password: string;
}

export interface LoginRequest {
    login: string;      // username or email
    password: string;
}

export interface TokenResponse {
    token: string;
}

export interface RequestPasswordReset {
    login: string;      // username or email
}

export interface ResetPassword {
    code: string;
    newPassword: string;
}

export interface ChangePassword {
    oldPassword: string;
    newPassword: string;
}