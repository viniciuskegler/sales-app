export interface LoginRequest {
    email: string;
    password: string;
}

export interface RegisterRequest {
    email: string;
    password: string;
    firstName: string;
    lastName: string;
    phoneNumber: string;
}

export interface UserDetails {
    id: number;
    email: string;
    role: string;
    fullName: string;
}

export interface AuthResponse {
    message: string;
    token: string;
    userDetails: UserDetails;
}
