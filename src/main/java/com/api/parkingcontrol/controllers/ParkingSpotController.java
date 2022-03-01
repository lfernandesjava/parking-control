package com.api.parkingcontrol.controllers;

import ch.qos.logback.core.joran.conditional.IfAction;
import com.api.parkingcontrol.dtos.ParkingSpotDto;
import com.api.parkingcontrol.models.ParkingSpotModel;
import com.api.parkingcontrol.services.ParkingSpotService;
import net.bytebuddy.TypeCache;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
@RequestMapping( "/parking-spot")
public class ParkingSpotController {

    final ParkingSpotService parkingSpotService;

    public ParkingSpotController(ParkingSpotService parkingSpotService) {
        this.parkingSpotService = parkingSpotService;
    }

    @PostMapping
    public ResponseEntity<Object> saveParkingSpot(@RequestBody @Valid ParkingSpotDto parkingSpotDto) {

        if(parkingSpotService.existsByLicencePlateCar(parkingSpotDto.getLicencePlateCar())){
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Conflito: Placa do carro ja esta em uso");
        }

        if (parkingSpotService.existsByParkingSpotNumber(parkingSpotDto.getParkingSpotNumber())){
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Conflito: Ja esta em uso");
        }

        if (parkingSpotService.existsByApartamentAndBlock(parkingSpotDto.getApartament(), parkingSpotDto.getBlock())){
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Conflito : Parking Spot ja esta registrado");
        }
        var parkingSpotModel = new ParkingSpotModel();
        BeanUtils.copyProperties(parkingSpotDto, parkingSpotModel);
        parkingSpotModel.setRegistrationDate(LocalDateTime.now(ZoneId.of("UTC")));
        return  ResponseEntity.status(HttpStatus.CREATED).body(parkingSpotService.save(parkingSpotModel));
    }
    @GetMapping
    public ResponseEntity<Page<ParkingSpotModel>> getAllParkingSpots(@PageableDefault(page = 0,size = 10, sort = "id",
                                                                    direction = Sort.Direction.ASC)Pageable pageable){
        return ResponseEntity.status(HttpStatus.OK).body(parkingSpotService.findAll(pageable));

    }

    @GetMapping( "/{id}")
    public ResponseEntity<Object> getOneParkingSpot(@PathVariable(value = "id")UUID id){
        Optional<ParkingSpotModel> parkingSpotModelOptional = parkingSpotService.findById(id);

        if (!parkingSpotModelOptional.isPresent()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Parking Spot Inexistente");
        }
        return ResponseEntity.status(HttpStatus.OK).body(parkingSpotModelOptional.get());
    }

    @DeleteMapping( "/{id}")
    public ResponseEntity<Object> deleteParkingSpot(@PathVariable(value = "id")UUID id){
        Optional<ParkingSpotModel> parkingSpotModelOptional = parkingSpotService.findById(id);
        if (!parkingSpotModelOptional.isPresent()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Não existe");
        }
        parkingSpotService.delete(parkingSpotModelOptional.get());
        return ResponseEntity.status(HttpStatus.OK).body("Deletado com sucesso");
    }

  /*  @PutMapping( "/{id}")
    public ResponseEntity<Object> updateParkingSpot(@PathVariable(value = "id") UUID id,@RequestBody @Valid ParkingSpotDto parkingSpotDto){
        Optional<ParkingSpotModel> parkingSpotModelOptional = parkingSpotService.findById(id);
        if (!parkingSpotModelOptional.isPresent()){
            return  ResponseEntity.status(HttpStatus.NOT_FOUND).body("nao encontrado");
        }
        var parkingSpotModel = parkingSpotModelOptional.get();
        parkingSpotModel.getParkingSpotNumber(parkingSpotDto.getParkingSpotNumber());
        parkingSpotModel.setLicencePlateCar(parkingSpotDto.getLicencePlateCar());
        parkingSpotModel.setModelCar(parkingSpotDto.getModelCar());
        parkingSpotModel.setBrandCar(parkingSpotDto.getBrandCar());
        parkingSpotModel.setColorCar(parkingSpotDto.getColorCar());
        parkingSpotModel.setResponsibleName(parkingSpotDto.getResponsibleName());
        parkingSpotModel.setApartament(parkingSpotDto.getApartament());
        parkingSpotModel.setBlock(parkingSpotDto.getBlock());

        return ResponseEntity.status(HttpStatus.OK).body(parkingSpotService.save(parkingSpotModel));

    }*/

     @PutMapping( "/{id}")
    public ResponseEntity<Object> updateParkingSpot(@PathVariable(value = "id") UUID id,@RequestBody @Valid ParkingSpotDto parkingSpotDto){
        Optional<ParkingSpotModel> parkingSpotModelOptional = parkingSpotService.findById(id);
        if (!parkingSpotModelOptional.isPresent()){
            return  ResponseEntity.status(HttpStatus.NOT_FOUND).body("nao encontrado");
        }
        var parkingSpotModel = new ParkingSpotModel();
        BeanUtils.copyProperties(parkingSpotDto, parkingSpotModel);
        parkingSpotModel.setId(parkingSpotModelOptional.get().getId());
        parkingSpotModel.setRegistrationDate(parkingSpotModelOptional.get().getRegistrationDate());
        return ResponseEntity.status(HttpStatus.OK).body(parkingSpotService.save(parkingSpotModel));

    }

}
