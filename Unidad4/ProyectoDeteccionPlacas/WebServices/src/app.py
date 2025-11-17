from flask import Flask
from flask_cors import CORS
from database import db
import json
import os

app = Flask(__name__)
CORS(app)

# conf para json utf-8
class CustomJSONEncoder(json.JSONEncoder):
    def encode(self, o):
        return json.dumps(o, ensure_ascii=False, indent=2)

app.json_encoder = CustomJSONEncoder
app.json.ensure_ascii = False

#DATABASE_URL = "postgresql://bddetectorplates_user:Je6U9C08KLCWhINAyfPVkVZaQi41t68L@dpg-d4d9h6qli9vc73cdf4s0-a.oregon-postgres.render.com/bddetectorplates"
#app.config['SQLALCHEMY_DATABASE_URI'] = DATABASE_URL
DATABASE_URL = os.environ.get("DATABASE_URL", "sqlite:///local_database.db")
app.config['SQLALCHEMY_DATABASE_URI'] = DATABASE_URL
app.config['SQLALCHEMY_TRACK_MODIFICATIONS'] = False

db.init_app(app)

# IMPORTAR MODELOS despues de db (evita import circular)
from models.Propietario import Propietario
from models.Vehiculos import Vehiculos
from models.Reporte import Reporte
from models.Usuario import Usuario
#Serivicios
from services.reporteServices import reporte_bp
from services.usuarioServices import user_bp
from services.vehiculoServices import vehiculo_bp
from services.propietarioServices import propietario_bp
# Planos de servicios
app.register_blueprint(reporte_bp)
app.register_blueprint(user_bp)
app.register_blueprint(vehiculo_bp)
app.register_blueprint(propietario_bp)

# Crear tablas si no existen
with app.app_context():
    db.create_all()

if __name__ == "__main__":
    app.run(host="0.0.0.0", port=5000, debug=True)