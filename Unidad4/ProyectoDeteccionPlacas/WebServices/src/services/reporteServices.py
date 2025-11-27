from flask import Blueprint, request, jsonify
from database import db
from models.Reporte import Reporte
from models.Vehiculos import Vehiculos
from models.Usuario import Usuario

reporte_bp = Blueprint('Reportes', __name__, url_prefix='/api/reportes')

@reporte_bp.route('/', methods=['POST'])
def crear_reporte():
    try:
        data = request.get_json()
        # Validar datos requeridos
        if not data.get('NumPlaca') or not data.get('UsuarioID'):
            return jsonify({'error': 'NumPlaca y UsuarioID son requeridos'}), 400
        
        # Verificar que el vehiculo y usuario existan
        vehiculo = Vehiculos.query.get(data['NumPlaca'])
        usuario = Usuario.query.get(data['UsuarioID'])
        if not vehiculo:
            return jsonify({'error': 'Vehiculo no encontrado'}), 404
        if not usuario:
            return jsonify({'error': 'Usuario no encontrado'}), 404
        
        # Crear reporte
        nuevo_reporte = Reporte(
            Coordenadas=data.get('Coordenadas'),
            Descripcion=data.get('Descripcion'),
            ImgEvidencia=data.get('ImgEvidencia'),
            NumPlaca=data['NumPlaca'],
            UsuarioID=data['UsuarioID']
        )
        db.session.add(nuevo_reporte)
        db.session.commit()
        
        return jsonify({
            'message': 'Reporte creado exitosamente',
            'reporte': nuevo_reporte.to_dict()
        }), 201
        
    except Exception as e:
        return jsonify({'error': str(e)}), 500
#url de api: /api/reportes/
@reporte_bp.route('/', methods=['GET'])
def get_reportes():
    try:
        reportes = Reporte.query.all()
        return jsonify([reporte.to_dict() for reporte in reportes]), 200
    except Exception as e:
        return jsonify({'error': str(e)}), 500

#url de api: /api/reportes/<int:reporte_id>
@reporte_bp.route('/<int:reporte_id>', methods=['GET'])
def get_reporte(reporte_id):
    try:
        reporte = Reporte.query.get_or_404(reporte_id)
        return jsonify(reporte.to_dict()), 200
    except Exception as e:
        return jsonify({'error': str(e)}), 500

#url de api: /api/reportes/placa/<string:num_placa>
@reporte_bp.route('/placa/<string:num_placa>', methods=['GET'])
def get_reportes_por_placa(num_placa):
    try:
        reportes = Reporte.query.filter_by(numplaca=num_placa).all()
        return jsonify([reporte.to_dict() for reporte in reportes]), 200
    except Exception as e:
        return jsonify({'error': str(e)}), 500
    
#url de api: /api/reportes/usuario/<int:usuario_id> 
@reporte_bp.route('/usuario/<int:usuario_id>', methods=['GET'])
def get_reportes_por_usuario(usuario_id):
    try:
        reportes = Reporte.query.filter_by(usuarioid=usuario_id).all()
        return jsonify([reporte.to_dict() for reporte in reportes]), 200
    except Exception as e:
        return jsonify({'error': str(e)}), 500