# simple_llm_bigram.py
# --------------------
# Usage:
#   1) Put some training text in input.txt (or it'll use a built-in snippet)
#   2) python simple_llm_bigram.py
#   3) After training, it will print a sample

import torch
import torch.nn as nn
import torch.nn.functional as F
from pathlib import Path

# ----- Load data -----
path = Path("input.txt")
if path.exists():
    text = path.read_text(encoding="utf-8")
else:
    text = """To be, or not to be, that is the question:
Whether 'tis nobler in the mind to suffer
The slings and arrows of outrageous fortune,"""

# ----- Char-level vocab -----
chars = sorted(list(set(text)))
vocab_size = len(chars)
stoi = {ch: i for i, ch in enumerate(chars)}
itos = {i: ch for ch, i in stoi.items()}

def encode(s): return torch.tensor([stoi[c] for c in s], dtype=torch.long)
def decode(t): return "".join(itos[int(i)] for i in t)

data = encode(text)

# Train/val split
n = int(0.9 * len(data))
train_data = data[:n]
val_data = data[n:]

# ----- Hyperparams -----
device = "cuda" if torch.cuda.is_available() else "cpu"
torch.manual_seed(1337)
batch_size = 64   # sequences per batch
epochs = 2000     # iterations
eval_interval = 200
lr = 1e-2

def get_batch(split):
    ds = train_data if split == "train" else val_data
    ix = torch.randint(len(ds) - 1, (batch_size,))
    # For bigram, we only use the current token to predict the next one
    x = ds[ix].unsqueeze(-1)     # (B, 1)
    y = ds[ix + 1]               # (B,)
    return x.to(device), y.to(device)

# ----- Bigram Language Model -----
class BigramLM(nn.Module):
    def __init__(self, vocab_size):
        super().__init__()
        # This embedding table directly stores logits for "next token" given current token
        self.token_embedding_table = nn.Embedding(vocab_size, vocab_size)

    def forward(self, idx, targets=None):
        # idx: (B, 1) current token
        logits = self.token_embedding_table(idx).squeeze(1)  # (B, vocab_size)
        loss = None
        if targets is not None:
            loss = F.cross_entropy(logits, targets)
        return logits, loss

    @torch.no_grad()
    def generate(self, start_idx, max_new_tokens=200):
        idx = torch.tensor([start_idx], device=device).unsqueeze(0)  # (1,1)
        out = [start_idx]
        for _ in range(max_new_tokens):
            logits, _ = self(idx)           # (1, vocab_size)
            probs = F.softmax(logits, dim=-1)
            next_idx = torch.multinomial(probs, num_samples=1)  # (1,1)
            out.append(int(next_idx.item()))
            idx = next_idx                  # feed the new char
        return torch.tensor(out, device=device)

def main():
    model = BigramLM(vocab_size).to(device)
    optimizer = torch.optim.AdamW(model.parameters(), lr=lr)

    # ----- Train -----
    for step in range(epochs + 1):
        model.train()
        xb, yb = get_batch("train")
        logits, loss = model(xb, yb)
        optimizer.zero_grad(set_to_none=True)
        loss.backward()
        optimizer.step()

        if step % eval_interval == 0:
            # quick val
            with torch.no_grad():
                model.eval()
                vxb, vyb = get_batch("val")
                _, vloss = model(vxb, vyb)
            print(f"step {step:4d} | train loss {loss.item():.4f} | val loss {vloss.item():.4f}")

    # ----- Sample -----
    # Start from a random character
    start_char = torch.randint(0, vocab_size, (1,)).item()
    sample = model.generate(start_char, max_new_tokens=400)
    print("\\n--- SAMPLE ---\\n")
    print(decode(sample))

if __name__ == "__main__":
    main()
