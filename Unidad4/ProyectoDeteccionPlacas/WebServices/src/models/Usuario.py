from app import db

class Usuario(db.Model):
    __tablename__ = 'Usuario'
    
    UsuarioID = db.Column(db.Integer, primary_key=True)  # SERIAL = autoincrement
    Nombre = db.Column(db.String(100), nullable=False)  
    Contrasena = db.Column(db.String(255), nullable=False)  
    Correo = db.Column(db.String(100), unique=True, nullable=False) 
    
    def __init__(self, Nombre, Contrasena, Correo):
        self.Nombre = Nombre
        self.Contrasena = Contrasena
        self.Correo = Correo
        
    def to_dict(self):
        return {
            'UsuarioID': self.UsuarioID,
            'Nombre': self.Nombre,
            'Correo': self.Correo
            # Nunca incluir la contraseña en el diccionario por seguridad
        }
        
    def __repr__(self):
        return f"<Usuario {self.UsuarioID}: {self.Nombre} - {self.Correo}>"