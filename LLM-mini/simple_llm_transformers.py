# simple_llm_transformers.py
# --------------------------
# pip install transformers torch --upgrade
# python simple_llm_transformers.py

from transformers import AutoModelForCausalLM, AutoTokenizer
import torch

model_name = "distilgpt2"  # small, quick to download
device = "cuda" if torch.cuda.is_available() else "cpu"

tok = AutoTokenizer.from_pretrained(model_name)
model = AutoModelForCausalLM.from_pretrained(model_name).to(device)
model.eval()

prompt = "As a senior QA Automation engineer, my test strategy is"
inputs = tok(prompt, return_tensors="pt").to(device)

with torch.no_grad():
    out = model.generate(
        **inputs,
        max_new_tokens=120,
        temperature=0.9,
        top_p=0.95,
        do_sample=True,
        eos_token_id=tok.eos_token_id
    )

print(tok.decode(out[0], skip_special_tokens=True))
