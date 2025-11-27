FROM python:3.9-slim
WORKDIR /app
RUN apt-get update && apt-get install -y libgl1-mesa-glx libglib2.0-0 && rm -rf /var/lib/apt/lists/*
COPY . .
RUN pip install --no-cache-dir -r requirements.txt
RUN useradd -m -u 1000 user
USER user
ENV HOME=/home/user PATH=/home/user/.local/bin:$PATH
EXPOSE 7860
CMD ["python", "app.py"]