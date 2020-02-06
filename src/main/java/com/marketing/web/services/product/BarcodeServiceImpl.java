package com.marketing.web.services.product;

import com.marketing.web.configs.constants.MessagesConstants;
import com.marketing.web.errors.ResourceNotFoundException;
import com.marketing.web.models.Barcode;
import com.marketing.web.models.Product;
import com.marketing.web.repositories.BarcodeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class BarcodeServiceImpl implements BarcodeService {

    @Autowired
    private BarcodeRepository barcodeRepository;

    @Override
    public List<Barcode> findAll() {
        return barcodeRepository.findAll();
    }

    @Override
    public List<Barcode> findByProduct(Product product) {
        return barcodeRepository.findAllByProduct(product);
    }

    @Override
    public Barcode findByProductAndBarcodeNo(Product product, String barcodeNo) {
        return barcodeRepository.findByProductAndBarcodeNo(product, barcodeNo).orElseThrow(() -> new ResourceNotFoundException(MessagesConstants.RESOURCES_NOT_FOUND+"product.barcode",barcodeNo));
    }

    @Override
    public Barcode findByBarcodeNo(String barcodeNo) {
        return barcodeRepository.findByBarcodeNo(barcodeNo).orElseThrow(() -> new ResourceNotFoundException(MessagesConstants.RESOURCES_NOT_FOUND+"product.barcode",barcodeNo));
    }

    @Override
    public Barcode checkByBarcodeNo(String barcodeNo) {
        return barcodeRepository.findByBarcodeNo(barcodeNo).orElse(null);
    }

    @Override
    public Barcode findById(Long id) {
        return barcodeRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(MessagesConstants.RESOURCES_NOT_FOUND+"product.barcode",id.toString()));
    }

    @Override
    public Barcode findByUuid(String uuid) {
        return barcodeRepository.findByUuid(UUID.fromString(uuid)).orElseThrow(() -> new ResourceNotFoundException(MessagesConstants.RESOURCES_NOT_FOUND+"product.barcode",uuid));
    }

    @Override
    public Barcode create(Barcode barcode) {
        return barcodeRepository.save(barcode);
    }

    @Override
    public Barcode update(String uuid, Barcode updatedBarcode) {
        Barcode barcode = findByUuid(uuid);
        barcode.setBarcodeNo(updatedBarcode.getBarcodeNo());
        barcode.setProduct(updatedBarcode.getProduct());
        return barcodeRepository.save(barcode);
    }

    @Override
    public void delete(Barcode barcode) {
        barcodeRepository.delete(barcode);
    }
}
