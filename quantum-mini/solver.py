import numpy as np
def make_grid(xmin, xmax, N):
    x = np.linspace(xmin, xmax, N)
    dx = x[1] - x[0]
    return x, dx
def kinetic_matrix(N, dx, hbar=1.0, m=1.0):
    diag = np.full(N, -2.0)
    off  = np.ones(N-1)
    T = (-(hbar**2)/(2.0*m)) * (np.diag(diag, 0) + np.diag(off, 1) + np.diag(off, -1)) / (dx**2)
    return T
def hamiltonian(V, dx, hbar=1.0, m=1.0):
    N = V.size
    T = kinetic_matrix(N, dx, hbar=hbar, m=m)
    H = T + np.diag(V)
    return H
def solve_bound_states(V, dx, k=5, hbar=1.0, m=1.0):
    H = hamiltonian(V, dx, hbar=hbar, m=m)
    E, psi = np.linalg.eigh(H)
    norm = np.sqrt(np.sum(np.abs(psi)**2, axis=0) * dx)
    psi = psi / norm
    return E[:k], psi[:, :k]
def V_infinite_well(x):
    return np.zeros_like(x)
def V_finite_square_well(x, V0=-50.0, a=2.0):
    return np.where(np.abs(x) <= a/2.0, V0, 0.0)
def V_harmonic_oscillator(x, m=1.0, omega=1.0):
    return 0.5 * m * (omega**2) * (x**2)
