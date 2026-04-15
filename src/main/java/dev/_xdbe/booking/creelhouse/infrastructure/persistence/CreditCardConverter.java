package dev._xdbe.booking.creelhouse.infrastructure.persistence;


import javax.crypto.IllegalBlockSizeException;
import javax.crypto.BadPaddingException;
import javax.crypto.NoSuchPaddingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.springframework.beans.factory.annotation.Autowired;

import dev._xdbe.booking.creelhouse.infrastructure.persistence.CryptographyHelper;


@Converter
public class CreditCardConverter implements AttributeConverter<String, String> {

    @Autowired
    private CryptographyHelper cryptographyHelper;

   @Override
    public String convertToDatabaseColumn(String attribute) {
        // Step 7a: Chiffre le PAN avant stockage
        if (attribute == null) return null;
        return CryptographyHelper.encryptData(attribute);
        // Step 7a: End
    }

    @Override
    public String convertToEntityAttribute(String dbData) {
        // Step 7b: Déchiffre le PAN puis applique le masque
        if (dbData == null) return null;
        
        // On récupère la donnée en clair depuis le cipher
        String decryptedPan = CryptographyHelper.decryptData(dbData);
        
        // On applique le masque (Step 6) sur la donnée déchiffrée
        return panMasking(decryptedPan);
        // Step 7b: End
    }

private String panMasking(String pan) {
        // Step 6:
        if (pan == null || pan.length() < 8) {
            return pan; 
        }

        // On garde les 4 premiers 
        String firstFour = pan.substring(0, 4);
        
        // On garde les 4 derniers 
        String lastFour = pan.substring(pan.length() - 4);
        
        // On remplace le milieu par des * [cite: 262, 272]
        String stars = "*".repeat(pan.length() - 8);

        return firstFour + stars + lastFour;
        // Step 6: End
    }

    
}