import os, numpy as np, matplotlib.pyplot as plt
from solver import make_grid, V_infinite_well, V_finite_square_well, V_harmonic_oscillator, solve_bound_states
def plot_states(x, E, psi, V, title, fname_prefix=None, max_states=4):
    n = min(max_states, psi.shape[1])
    plt.figure(figsize=(7,4.5))
    V_shift = V
    plt.plot(x, V_shift, label="V(x)")
    for n_idx in range(n):
        y = E[n_idx] + np.abs(psi[:, n_idx])**2 * (0.6 * (np.max(V_shift)-np.min(V_shift) + 1))
        plt.plot(x, y, label=f"|ψ_{n_idx}(x)|² @ E={E[n_idx]:.2f}")
    plt.title(title); plt.xlabel("x"); plt.ylabel("Energy / Probability Density (scaled)"); plt.legend(loc="best")
    if fname_prefix: plt.savefig(f"{fname_prefix}.png", dpi=160, bbox_inches="tight")
    plt.show()
def main():
    outdir = os.path.dirname(__file__) or "."
    xmin, xmax, N = -5.0, 5.0, 500
    x, dx = make_grid(xmin, xmax, N)
    V_inf = V_infinite_well(x)
    E_inf, psi_inf = solve_bound_states(V_inf, dx, k=4)
    plot_states(x, E_inf, psi_inf, V_inf, "Infinite Well (Dirichlet boundaries)", fname_prefix=os.path.join(outdir, "infinite_well"))
    V_fin = V_finite_square_well(x, V0=-50.0, a=2.0)
    E_fin, psi_fin = solve_bound_states(V_fin, dx, k=4)
    plot_states(x, E_fin, psi_fin, V_fin, "Finite Square Well (V0=-50, a=2.0)", fname_prefix=os.path.join(outdir, "finite_well"))
    V_ho = V_harmonic_oscillator(x, m=1.0, omega=1.0)
    E_ho, psi_ho = solve_bound_states(V_ho, dx, k=4)
    plot_states(x, E_ho, psi_ho, V_ho, "Harmonic Oscillator (m=1, ω=1)", fname_prefix=os.path.join(outdir, "harmonic_oscillator"))
if __name__ == "__main__":
    main()
