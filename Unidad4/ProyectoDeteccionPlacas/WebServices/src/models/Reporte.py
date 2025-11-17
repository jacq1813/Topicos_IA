from database import db
from datetime import datetime

class Reporte(db.Model):
    __tablename__ = 'reporte'
    
    reporteid = db.Column(db.Integer, primary_key=True)
    fechaemision = db.Column(db.DateTime, nullable=False, default=datetime.utcnow)
    coordenadas = db.Column(db.String(100), nullable=True)
    descripcion = db.Column(db.Text, nullable=True)
    imgevidencia = db.Column(db.String(255), nullable=True)
    numplaca = db.Column(db.String(20), db.ForeignKey('vehiculos.numplaca'), nullable=False)
    usuarioid = db.Column(db.Integer, db.ForeignKey('usuario.usuarioid'), nullable=False)
    
    # Relaciones
    vehiculo = db.relationship('Vehiculos', backref='reportes')
    usuario = db.relationship('Usuario', backref='reportes')
    
    def __init__(self, Coordenadas, Descripcion, ImgEvidencia, NumPlaca, UsuarioID, FechaEmision=None):
        self.fechaemision = FechaEmision or datetime.utcnow()
        self.coordenadas = Coordenadas
        self.descripcion = Descripcion
        self.imgevidencia = ImgEvidencia
        self.numplaca = NumPlaca
        self.usuarioid = UsuarioID
        
    def to_dict(self):
        return {
            'ReporteID': self.reporteid,
            'FechaEmision': self.fechaemision.isoformat() if self.fechaemision else None,
            'Coordenadas': self.coordenadas,
            'Descripcion': self.descripcion,
            'ImgEvidencia': self.imgevidencia,
            'NumPlaca': self.numplaca,
            'UsuarioID': self.usuarioid
        }
        
    def __repr__(self):
        return f"<Reporte {self.reporteid}: Placa {self.numplaca} - {self.fechaemision}>"