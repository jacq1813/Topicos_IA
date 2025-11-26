from flask import Blueprint, request, jsonify
from database import db
from models.Usuario import Usuario
from werkzeug.security import generate_password_hash, check_password_hash

user_bp = Blueprint('Usuarios', __name__, url_prefix='/api/usuarios')

@user_bp.route('/register', methods=['POST'])
def register_user():
    try:
        data = request.get_json()
        
        if not data.get('Nombre') or not data.get('Correo') or not data.get('Contrasena'):
            return jsonify({'error': 'Nombre, Correo y Contraseña son requeridos'}), 400
        
        if Usuario.query.filter_by(correo=data['Correo']).first():
            return jsonify({'error': 'El correo ya está registrado'}), 400
        
        hashed_password = generate_password_hash(data['Contrasena'])
        
        nuevo_usuario = Usuario(
            Nombre=data['Nombre'],      # <--- Nombre con N mayúscula
            Correo=data['Correo'],      # <--- Correo con C mayúscula
            Contrasena=hashed_password  # <--- Contrasena con C mayúscula
        )
        
        db.session.add(nuevo_usuario)
        db.session.commit()
        
        return jsonify({
            'message': 'Usuario registrado exitosamente',
            'Usuario': nuevo_usuario.to_dict()
        }), 201
        
    except Exception as e:
        return jsonify({'error': str(e)}), 500

@user_bp.route('/login', methods=['POST'])
def login_user():
    try:
        data = request.get_json()
        
        # Validación correcta (Android envía Mayúsculas)
        if not data.get('Correo') or not data.get('Contrasena'):
            return jsonify({'error': 'Correo y Contraseña son requeridos'}), 400
        
        # Filtro correcto (Columna BD es minúscula, dato Android es Mayúscula)
        usuario = Usuario.query.filter_by(correo=data['Correo']).first()
        
        # CAMBIO AQUÍ: usuario.contrasena debe ser minúscula
        if usuario and check_password_hash(usuario.contrasena, data['Contrasena']):
            return jsonify({
                'message': 'Login exitoso',
                'Usuario': usuario.to_dict()
            }), 200
        else:
            return jsonify({'error': 'Credenciales inválidas'}), 401
            
    except Exception as e:
        return jsonify({'error': str(e)}), 500
    
@user_bp.route('/', methods=['GET'])
def get_users():
    try:
        usuarios = Usuario.query.all()
        return jsonify([usuario.to_dict() for usuario in usuarios]), 200
    except Exception as e:
        return jsonify({'error': str(e)}), 500