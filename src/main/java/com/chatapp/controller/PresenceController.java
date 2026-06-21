package com.chatapp.controller;

import com.chatapp.data.entity.UserPresence;
import com.chatapp.service.PresenceService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/presence")
@RequiredArgsConstructor
public class PresenceController {

    private final PresenceService presenceService;

    @PostMapping("/{id}/online")
    public void online(@PathVariable Long id, @RequestParam String session){
        presenceService.markOnline(id,session);
    }



    @PostMapping("/{id}/offline")
    public void offline(@PathVariable Long id){
        presenceService.markOffline(id);
    }



    @PostMapping("/{id}/away")
    public void away(@PathVariable Long id){
        presenceService.markAway(id);
    }



    @PostMapping("/{id}/busy")
    public void busy(@PathVariable Long id){
        presenceService.markBusy(id);
    }



    @GetMapping("/{id}")
    public UserPresence status(@PathVariable Long id){
        return presenceService.getPresence(id);
    }

}