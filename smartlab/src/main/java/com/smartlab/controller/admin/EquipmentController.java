package com.smartlab.controller.admin;

import com.smartlab.entity.Equipment;
import com.smartlab.service.EquipmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/equipments")
public class EquipmentController {

    private final EquipmentService service;

    @GetMapping
    public String list(Model model){model.addAttribute("equipments", service.getAll());

        return "admin/equipment/list";
    }

    @GetMapping("/create")
    public String createPage(Model model){

        model.addAttribute("equipment", new Equipment());

        return "admin/equipment/create";
    }

    @PostMapping("/create")
    public String create(@ModelAttribute Equipment equipment){

        service.create(equipment);

        return "redirect:/admin/equipments";
    }

    @GetMapping("/edit/{id}")
    public String editPage(@PathVariable Long id, Model model){

        model.addAttribute("equipment", service.findById(id));

        return "admin/equipment/edit";
    }

    @PostMapping("/edit")
    public String edit(@ModelAttribute Equipment equipment){

        service.update(equipment);

        return "redirect:/admin/equipments";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id){

        service.delete(id);

        return "redirect:/admin/equipments";
    }
}