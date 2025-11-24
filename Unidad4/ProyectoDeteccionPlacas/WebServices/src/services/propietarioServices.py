from flask import Blueprint, request, jsonify
from database import db
from models.Propietario import Propietario

propietario_bp = Blueprint('Propietarios', __name__, url_prefix='/api/propietarios')

@propietario_bp.route('/', methods=['GET'])
def obtener_propietarios():
    try:
        propietarios = Propietario.query.all()
        return jsonify([propietario.to_dict() for propietario in propietarios]), 200
    except Exception as e:
        return jsonify({'error': str(e)}), 500
    
@propietario_bp.route('/<int:propietario_id>', methods=['GET'])
def obtener_propietario(propietario_id):
    try:
        propietario = Propietario.query.get_or_404(propietario_id)
        return jsonify(propietario.to_dict()), 200
    except Exception as e:
        return jsonify({'error': str(e)}), 500
    