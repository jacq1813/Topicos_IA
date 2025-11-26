from flask import Blueprint, request, jsonify
from database import db
from models.Usuario import Usuario
from werkzeug.security import generate_password_hash, check_password_hash

user_bp = Blueprint('Usuarios', __name__, url_prefix='/api/usuarios')

@user_bp.route('/register', methods=['POST'])
def register_user():
    try:
        data = request.get_json()
        
        # Validar datos requeridos
        if not data.get('Nombre') or not data.get('Correo') or not data.get('Contrasena'):
            return jsonify({'error': 'Nombre, Correo y Contraseña son requeridos'}), 400
        
        # Verificar si el usuario ya existe
        if Usuario.query.filter_by(correo=data['Correo']).first():
            return jsonify({'error': 'El correo ya está registrado'}), 400
        
        # Hashear contraseña
        hashed_password = generate_password_hash(data['Contrasena'])
        
        # Crear usuario
        nuevo_usuario = Usuario(
            Nombre=data['Nombre'],
            Correo=data['Correo'],
            Contrasena=hashed_password
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
        
        if not data.get('Correo') or not data.get('Contrasena'):
            return jsonify({'error': 'Correo y Contraseña son requeridos'}), 400
        
        usuario = Usuario.query.filter_by(correo=data['Correo']).first()
        
        if usuario and check_password_hash(usuario.Contrasena, data['Contrasena']):
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