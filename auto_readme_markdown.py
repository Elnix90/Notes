"""
Outputs all images from the screenshots folder in a Markdown table for the README.
"""


from pathlib import Path

FOLDER = Path("/home/elnix/IdeaProjects/Notes/screenshots")

# Collect images in folder order
images: list[str] = []
names: list[str] = []

for file in FOLDER.iterdir():
    if file.suffix.lower() in (".png", ".jpg", ".jpeg", ".gif"):
        folder_name = FOLDER.name
        alt_text = file.stem.replace("_", " ").capitalize()
        images.append(f'<img src="{folder_name}/{file.name}" alt="{alt_text}"/>')
        names.append(alt_text)

# Build table header
HEADER_ROW = "| " + " | ".join(names) + " |"
SEPARATOR_ROW = "|" + "|".join(["-" * (len(n) + 2) for n in names]) + "|"
IMAGE_ROW = "| " + " | ".join(images) + " |"

# Output full table
print(HEADER_ROW)
print(SEPARATOR_ROW)
print(IMAGE_ROW)
