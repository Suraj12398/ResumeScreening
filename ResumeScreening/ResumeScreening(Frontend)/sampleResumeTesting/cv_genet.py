import os
import random
from faker import Faker
from reportlab.lib.pagesizes import letter
from reportlab.pdfgen import canvas
from datetime import datetime

# Initialize Faker
fake = Faker()

# Directory to save CVs
output_dir = "generated_cvs"
os.makedirs(output_dir, exist_ok=True)

# IT-related skills
it_skills = [
    "Python", "Java", "C++", "SQL", "Machine Learning", "Deep Learning", "Cloud Computing",
    "Cybersecurity", "Data Engineering", "DevOps", "Docker", "Kubernetes", "React", "Node.js",
    "Django", "Flask", "TensorFlow", "PyTorch", "Blockchain", "IoT"
]

def generate_experience_dates():
    """Generate random experience start and end dates."""
    start_date = fake.date_between(start_date="-15y", end_date="-1y")
    end_date = fake.date_between(start_date=start_date, end_date="today")
    return f"{start_date.strftime('%b-%Y')} to {end_date.strftime('%b-%Y')}"

def draw_wrapped_text(c, text, x, y, max_width, line_height):
    """Draw wrapped text within the given max width."""
    words = text.split()
    line = ""
    for word in words:
        if c.stringWidth(line + " " + word, "Helvetica", 12) < max_width:
            line += " " + word
        else:
            c.drawString(x, y, line.strip())
            y -= line_height
            line = word
    if line:
        c.drawString(x, y, line.strip())
    return y  # Return the last Y position used

def generate_random_cv(file_path, template_type):
    """Generate a random CV and save it as a PDF."""
    c = canvas.Canvas(file_path, pagesize=letter)
    width, height = letter
    y_position = height - 50  # Starting Y position

    # Generate random details
    name = fake.name()
    email = fake.email()
    phone = fake.phone_number()
    address = fake.address()
    skills = ", ".join(random.sample(it_skills, 5))
    experience_period = generate_experience_dates()
    experience = fake.sentence(nb_words=10)
    education = fake.sentence(nb_words=8)
    company = fake.company()
    job_title = fake.job()

    c.setFont("Helvetica-Bold", 16)
    c.drawString(100, y_position, name)
    y_position -= 30  # Move down

    c.setFont("Helvetica", 12)
    c.drawString(100, y_position, f"Email: {email}")
    y_position -= 20
    c.drawString(100, y_position, f"Phone: {phone}")
    y_position -= 20
    c.drawString(100, y_position, f"Address: {address}")
    y_position -= 30

    c.drawString(100, y_position, f"Skills: {skills}")
    y_position -= 30

    c.setFont("Helvetica-Bold", 14)
    c.drawString(100, y_position, "Experience:")
    y_position -= 20

    c.setFont("Helvetica", 12)
    wrapped_text = f"Job Title: {job_title}\nCompany: {company}\nExperience: {experience}\n{experience_period}"
    y_position = draw_wrapped_text(c, wrapped_text, 100, y_position, width - 150, 15) - 20

    c.setFont("Helvetica-Bold", 14)
    c.drawString(100, y_position, "Education:")
    y_position -= 20

    c.setFont("Helvetica", 12)
    y_position = draw_wrapped_text(c, education, 100, y_position, width - 150, 15)

    c.save()

# Generate 50 random CVs with different formats
for i in range(50):
    file_name = f"cv_{i+1}.pdf"
    file_path = os.path.join(output_dir, file_name)
    template_type = random.choice([1, 2])
    generate_random_cv(file_path, template_type)
    print(f"Generated: {file_path} with Template {template_type}")

print("All CVs have been generated and saved in the 'generated_cvs' folder.")
 