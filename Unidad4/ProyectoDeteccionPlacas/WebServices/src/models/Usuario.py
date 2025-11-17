from database import db

class Usuario(db.Model):
    __tablename__ = 'usuario'
    
    usuarioid = db.Column(db.Integer, primary_key=True)
    nombre = db.Column(db.String(100), nullable=False)
    contrasena = db.Column(db.String(255), nullable=False)
    correo = db.Column(db.String(100), unique=True, nullable=False)
    
    def __init__(self, Nombre, Contrasena, Correo):
        self.nombre = Nombre
        self.contrasena = Contrasena
        self.correo = Correo
        
    def to_dict(self):
        return {
            'UsuarioID': self.usuarioid,
            'Nombre': self.nombre,
            'Correo': self.correo
        }
        
    def __repr__(self):
        return f"<Usuario {self.usuarioid}: {self.nombre} - {self.correo}>"