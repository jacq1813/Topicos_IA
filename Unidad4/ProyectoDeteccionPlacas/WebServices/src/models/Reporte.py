from app import db
from datetime import datetime

class Reporte(db.Model):
    __tablename__ = 'Reporte'
    
    ReporteID = db.Column(db.Integer, primary_key=True) 
    FechaEmision = db.Column(db.DateTime, nullable=False, default=datetime.utcnow)
    Coordenadas = db.Column(db.String(100), nullable=True)
    Descripcion = db.Column(db.Text, nullable=True)
    ImgEvidencia = db.Column(db.String(255), nullable=True)  # URL de Cloudinary
    NumPlaca = db.Column(db.String(20), db.ForeignKey('Vehiculos.NumPlaca'), nullable=False)
    UsuarioID = db.Column(db.Integer, db.ForeignKey('Usuario.UsuarioID'), nullable=False)
    
    # Relaciones
    vehiculo = db.relationship('Vehiculos', backref='reportes')
    usuario = db.relationship('Usuario', backref='reportes')
    
    def __init__(self, Coordenadas, Descripcion, ImgEvidencia, NumPlaca, UsuarioID, FechaEmision=None):
        self.FechaEmision = FechaEmision or datetime.utcnow()
        self.Coordenadas = Coordenadas
        self.Descripcion = Descripcion
        self.ImgEvidencia = ImgEvidencia
        self.NumPlaca = NumPlaca
        self.UsuarioID = UsuarioID
        
    def to_dict(self):
        return {
            'ReporteID': self.ReporteID,
            'FechaEmision': self.FechaEmision.isoformat() if self.FechaEmision else None,
            'Coordenadas': self.Coordenadas,
            'Descripcion': self.Descripcion,
            'ImgEvidencia': self.ImgEvidencia,
            'NumPlaca': self.NumPlaca,
            'UsuarioID': self.UsuarioID
        }
        
    def __repr__(self):
        return f"<Reporte {self.ReporteID}: Placa {self.NumPlaca} - {self.FechaEmision}>"