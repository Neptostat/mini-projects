from urllib.parse import urlparse

def extract_channel(url: str) -> str:
    parsed = urlparse(url)
    path_parts = parsed.path.strip("/").split("/")

    if not path_parts:
        return None

    if path_parts[0].startswith("@"):
        return path_parts[0][1:]   # remove '@'
    if path_parts[0] in ("channel", "c", "user") and len(path_parts) > 1:
        return path_parts[1]

    return None

# Example list of YouTube URLs
urls = [
    "https://www.youtube.com/@/videos",
    "https://www.youtube.com/@/videos",
    ]

# Extract and join into quoted, comma-delimited list
channels = [f'"{extract_channel(u)}"' for u in urls if extract_channel(u)]
result = ",".join(channels)

print(result)
