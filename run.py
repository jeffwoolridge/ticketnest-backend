#!/usr/bin/env python3
import subprocess
import secrets
import os

jwt_secret = secrets.token_hex(32)
print(f"JWT_SECRET: {jwt_secret}")

os.environ["JWT_SECRET"] = jwt_secret
subprocess.run(["./mvnw", "spring-boot:run"], env=os.environ.copy())