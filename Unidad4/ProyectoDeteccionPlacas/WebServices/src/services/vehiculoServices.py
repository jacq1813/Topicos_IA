from flask import Blueprint, request, jsonify
from database import db
from models.Vehiculos import Vehiculos

vehiculo_bp = Blueprint('Vehiculos', __name__, url_prefix='/api/vehiculos')

@vehiculo_bp.route('/', methods=['GET'])
def obtener_vehiculos():
    try:
        vehiculos = Vehiculos.query.all()
        return jsonify([vehiculo.to_dict() for vehiculo in vehiculos]), 200
    except Exception as e:
        return jsonify({'error': str(e)}), 500
    
@vehiculo_bp.route('/<string:num_placa>', methods=['GET'])
def obtener_vehiculo(num_placa):
    try:
        vehiculo = Vehiculos.query.get_or_404(num_placa)
        return jsonify(vehiculo.to_dict()), 200
    except Exception as e:
        return jsonify({'error': str(e)}), 500
    
    
    