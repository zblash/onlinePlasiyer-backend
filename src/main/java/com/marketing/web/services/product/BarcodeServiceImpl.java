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

    private final BarcodeRepository barcodeRepository;

    public BarcodeServiceImpl(BarcodeRepository barcodeRepository) {
        this.barcodeRepository = barcodeRepository;
    }

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
    public Barcode findById(String id) {
        return barcodeRepository.findById(UUID.fromString(id)).orElseThrow(() -> new ResourceNotFoundException(MessagesConstants.RESOURCES_NOT_FOUND+"product.barcode",id.toString()));
    }

    @Override
    public Barcode create(Barcode barcode) {
        return barcodeRepository.save(barcode);
    }

    @Override
    public Barcode update(String id, Barcode updatedBarcode) {
        Barcode barcode = findById(id);
        barcode.setBarcodeNo(updatedBarcode.getBarcodeNo());
        barcode.setProduct(updatedBarcode.getProduct());
        return barcodeRepository.save(barcode);
    }

    @Override
    public void delete(Barcode barcode) {
        barcodeRepository.delete(barcode);
    }
}
