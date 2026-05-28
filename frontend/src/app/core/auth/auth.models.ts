export interface LoginRequest {
  username: string;
  password: string;
}

export interface RegisterRequest {
  username: string;
  email: string;
  password: string;
}

export interface LoginResponse {
  token: string;
  tokenType: string;
  username: string;
  roles: string[];
}

export interface CurrentUser {
  id: number;
  username: string;
  email: string;
  roles: string[];
  enabled: boolean;
}
