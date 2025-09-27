# Simple LLMs

Two minimal options:

1) **Bigram from scratch** (`simple_llm_bigram.py`): learns next-character probabilities from your `input.txt`. Great for understanding fundamentals.
2) **Pretrained with transformers** (`simple_llm_transformers.py`): loads a small model and generates text immediately.

## Quickstart

### Option A — Bigram (from scratch)

```bash
# (optional) create venv
python -m venv .venv && . .venv/Scripts/activate  # Windows
# or: source .venv/bin/activate                   # macOS/Linux

pip install torch --upgrade
python simple_llm_bigram.py
```

- Add your own corpus in `input.txt` to change the style.
- Tweak `epochs`, `batch_size`, etc., at the top of the script.

### Option B — Pretrained (transformers)

```bash
# (optional) create venv
python -m venv .venv && . .venv/Scripts/activate  # Windows
# or: source .venv/bin/activate                   # macOS/Linux

pip install transformers torch --upgrade
python simple_llm_transformers.py
```

## Files

- `simple_llm_bigram.py` — tiny educational model trained from scratch.
- `simple_llm_transformers.py` — loads `distilgpt2` and generates.
- `input.txt` — optional training text for the bigram model.
- `requirements.txt` — handy for the transformers example.
