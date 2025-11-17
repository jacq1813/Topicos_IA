from flask import Flask
from flask_cors import CORS
from flask_sqlalchemy import SQLAlchemy

app = Flask(__name__)
CORS(app)

DATABASE_URL = "postgresql://bddetectorplates_user:Je6U9C08KLCWhINAyfPVkVZaQi41t68L@dpg-d4d9h6qli9vc73cdf4s0-a.oregon-postgres.render.com/bddetectorplates"
app.config['SQLALCHEMY_DATABASE_URI'] = DATABASE_URL
app.config['SQLALCHEMY_TRACK_MODIFICATIONS'] = False
db = SQLAlchemy(app)

# IMPORTAR MODELOS despeus de db (evita import circular)
from models.Propietario import Propietario
from models.Vehiculos import Vehiculos
from models.Reporte import Reporte
# Cuando crees el modelo Usuario, también agrégalo aquí

# Crear tablas si no existen
with app.app_context():
    db.create_all()

if __name__ == "__main__":
    app.run(host="0.0.0.0", port=5000, debug=True)